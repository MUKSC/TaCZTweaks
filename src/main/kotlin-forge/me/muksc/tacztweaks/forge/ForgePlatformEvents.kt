package me.muksc.tacztweaks.forge

import me.muksc.tacztweaks.feature.datapack.shield.CustomShieldResult
import me.muksc.tacztweaks.mixininterface.feature.datapack.shield.CustomShieldEntity
import me.muksc.tacztweaks.platform.PlatformEvents
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraftforge.event.OnDatapackSyncEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.ShieldBlockEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.level.BlockEvent
import net.minecraftforge.eventbus.api.EventPriority
import thedarkcolour.kotlinforforge.forge.FORGE_BUS

object ForgePlatformEvents : PlatformEvents {
    override fun onPlayerJoin(callback: (player: ServerPlayer) -> Unit) {
        FORGE_BUS.addListener<PlayerEvent.PlayerLoggedInEvent> { event ->
            val player = event.entity as? ServerPlayer ?: return@addListener
            callback(player)
        }
    }

    override fun onServerTick(callback: (server: MinecraftServer) -> Unit) {
        FORGE_BUS.addListener<TickEvent.ServerTickEvent> { event ->
            if (event.phase != TickEvent.Phase.END) return@addListener
            callback(event.server)
        }
    }

    override fun onPlayerLeave(callback: (player: ServerPlayer) -> Unit) {
        FORGE_BUS.addListener<PlayerEvent.PlayerLoggedOutEvent> { event ->
            val player = event.entity as? ServerPlayer ?: return@addListener
            callback(player)
        }
    }

    override fun onLevelTick(callback: (level: Level) -> Unit) {
        FORGE_BUS.addListener<TickEvent.LevelTickEvent> { event ->
            if (event.phase != TickEvent.Phase.END) return@addListener
            callback(event.level)
        }
    }

    override fun onBlockBreak(callback: (level: Level, pos: BlockPos) -> Unit) {
        FORGE_BUS.addListener<BlockEvent.BreakEvent>(EventPriority.LOWEST) { event ->
            if (event.isCanceled) return@addListener
            val level = event.level as? Level ?: return@addListener
            callback(level, event.pos)
        }
    }

    override fun onShieldBlock(callback: (entity: LivingEntity, source: DamageSource, damage: Float) -> CustomShieldResult) {
        FORGE_BUS.addListener<ShieldBlockEvent>(EventPriority.LOWEST) { event ->
            val ext = CustomShieldEntity.of(event.entity)
            val currentDamage = event.originalBlockedDamage - event.blockedDamage
            val result = callback(event.entity, event.damageSource, currentDamage)
            if (result is CustomShieldResult.Blocked) {
                val blockedDamage = event.originalBlockedDamage - result.damage
                if (blockedDamage <= 0) return@addListener
                event.blockedDamage = blockedDamage
                event.setShieldTakesDamage(true)
            }
            ext.`tacztweaks$setShieldResult`(result)
        }
    }

    override fun onDataPackSync(callback: (player: ServerPlayer) -> Unit) {
        FORGE_BUS.addListener<OnDatapackSyncEvent> { event ->
            val players = event.player?.let(::listOf) ?: event.playerList.players
            for (player in players) {
                callback(player)
            }
        }
    }
}