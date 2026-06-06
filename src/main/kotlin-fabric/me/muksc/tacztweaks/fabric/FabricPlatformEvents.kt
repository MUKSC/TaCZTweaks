package me.muksc.tacztweaks.fabric

import me.muksc.tacztweaks.fabric.event.ShieldBlockEvent
import me.muksc.tacztweaks.feature.datapack.shield.CustomShieldResult
import me.muksc.tacztweaks.mixininterface.feature.datapack.shield.CustomShieldEntity
import me.muksc.tacztweaks.platform.PlatformEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level

object FabricPlatformEvents : PlatformEvents {
    override fun onPlayerJoin(callback: (player: ServerPlayer) -> Unit) {
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            callback(handler.player)
        }
    }

    override fun onServerTick(callback: (server: MinecraftServer) -> Unit) {
        ServerTickEvents.END_SERVER_TICK.register { server ->
            callback(server)
        }
    }

    override fun onPlayerLeave(callback: (player: ServerPlayer) -> Unit) {
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            callback(handler.player)
        }
    }

    override fun onLevelTick(callback: (level: Level) -> Unit) {
        ServerTickEvents.END_WORLD_TICK.register { level ->
            callback(level)
        }
    }

    override fun onBlockBreak(callback: (level: Level, pos: BlockPos) -> Unit) {
        PlayerBlockBreakEvents.AFTER.register { level, player, pos, state, entity ->
            callback(level, pos)
        }
    }

    override fun onShieldBlock(callback: (entity: LivingEntity, source: DamageSource, damage: Float) -> CustomShieldResult) {
        ShieldBlockEvent.CALLBACK.register { entity, source, amount ->
            val ext = CustomShieldEntity.of(entity)
            val result = callback(entity, source, amount)
            ext.`tacztweaks$setShieldResult`(result)
        }
    }

    override fun onDataPackSync(callback: (player: ServerPlayer) -> Unit) {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register { player, joined ->
            callback(player)
        }
    }
}