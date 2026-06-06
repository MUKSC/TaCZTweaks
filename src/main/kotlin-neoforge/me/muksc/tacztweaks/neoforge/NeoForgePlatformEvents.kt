package me.muksc.tacztweaks.neoforge

import me.muksc.tacztweaks.feature.datapack.shield.CustomShieldResult
import me.muksc.tacztweaks.mixininterface.feature.datapack.shield.CustomShieldEntity
import me.muksc.tacztweaks.platform.PlatformEvents
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.neoforged.bus.api.EventPriority
import net.neoforged.neoforge.event.OnDatapackSyncEvent
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.event.tick.LevelTickEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS

object NeoForgePlatformEvents : PlatformEvents {
    override fun onPlayerJoin(callback: (player: ServerPlayer) -> Unit) {
        FORGE_BUS.addListener<PlayerEvent.PlayerLoggedInEvent> { event ->
            val player = event.entity as? ServerPlayer ?: return@addListener
            callback(player)
        }
    }

    override fun onServerTick(callback: (server: MinecraftServer) -> Unit) {
        FORGE_BUS.addListener<ServerTickEvent.Post> { event ->
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
        FORGE_BUS.addListener<LevelTickEvent.Post> { event ->
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
        FORGE_BUS.addListener<LivingShieldBlockEvent>(EventPriority.LOWEST) { event ->
            val ext = CustomShieldEntity.of(event.entity)
            val currentDamage = event.originalBlockedDamage - event.blockedDamage
            val result = callback(event.entity, event.damageSource, currentDamage)
            if (result is CustomShieldResult.Blocked) {
                val blockedDamage = event.originalBlockedDamage - result.damage
                if (blockedDamage <= 0) return@addListener
                event.blockedDamage = blockedDamage
                event.setShieldDamage(result.durabilityDamage.apply(event.shieldDamage()))
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