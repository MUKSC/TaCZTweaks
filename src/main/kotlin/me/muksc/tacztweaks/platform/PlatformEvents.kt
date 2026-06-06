package me.muksc.tacztweaks.platform

import me.muksc.tacztweaks.feature.datapack.shield.CustomShieldResult
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level

interface PlatformEvents {
    fun onPlayerJoin(callback: (player: ServerPlayer) -> Unit)

    fun onServerTick(callback: (server: MinecraftServer) -> Unit)

    fun onPlayerLeave(callback: (player: ServerPlayer) -> Unit)

    fun onLevelTick(callback: (level: Level) -> Unit)

    fun onBlockBreak(callback: (level: Level, pos: BlockPos) -> Unit)

    fun onShieldBlock(callback: (entity: LivingEntity, source: DamageSource, damage: Float) -> CustomShieldResult)

    fun onDataPackSync(callback: (player: ServerPlayer) -> Unit)
}