@file:JvmName("RealisticInaccuracy")
package me.muksc.tacztweaks.feature.gameplay.behaviour

import com.tacz.guns.api.entity.IGunOperator
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.mixin.accessor.InaccuracyTypeAccessor
import me.muksc.tacztweaks.mixininterface.feature.synced_slide.SlideDataHolder
import me.muksc.tacztweaks.mixininterop.shouldSlide
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Pose

fun getRealisticInaccuracy(map: Map<InaccuracyType, Float>, entity: LivingEntity): Float {
    val types = getInaccuracyTypes(entity)
    val base = map[InaccuracyType.STAND] ?: 0.0F

    var inaccuracy = base
    for (type in types) {
        val value = map[type] ?: 0.0F
        if (base > 0) {
            inaccuracy *= value / base
        } else {
            inaccuracy += value
        }
    }
    return inaccuracy
}

private fun getInaccuracyTypes(entity: LivingEntity): List<InaccuracyType> = buildList {
    val operator = IGunOperator.fromLivingEntity(entity)
    val shouldSlide = SlideDataHolder.of(entity).shouldSlide
    if (InaccuracyTypeAccessor.`tacztweaks$invokeIsMove`(entity)) add(InaccuracyType.MOVE)
    if (entity.pose == Pose.CROUCHING) add(InaccuracyType.SNEAK)
    if (entity.pose == Pose.SWIMMING && !entity.isSwimming) add(InaccuracyType.LIE)
    if (operator.synAimingProgress >= 1.0F) add(InaccuracyType.AIM)
    if (Config.Gameplay.Behaviour.tiltRework() && shouldSlide) add(InaccuracyType.SNEAK)
}