@file:JvmName("DynamicPitchLimit")
package me.muksc.tacztweaks.feature.gameplay.crawl

import net.minecraft.client.Minecraft
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult
import kotlin.math.acos

fun getDynamicPitchLimitOrNull(): Double? {
    val player = Minecraft.getInstance().player ?: return null
    val result = player.clientLevel.clip(ClipContext(
        player.eyePosition,
        player.eyePosition.add(player.lookAngle.scale(1.5)),
        ClipContext.Block.COLLIDER,
        ClipContext.Fluid.NONE,
        player
    ))
    if (result.type == HitResult.Type.MISS) return null
    val distance = result.location.distanceTo(player.eyePosition)
    return acos(distance)
}