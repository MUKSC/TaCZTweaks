package me.muksc.tacztweaks.feature.general.compatibility

import com.tacz.guns.entity.EntityKineticBullet
import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager
import me.muksc.tacztweaks.mixininterface.feature.raytracer.RayTracingBullet

object LegendarySurvivalOverhaulManager : ModCompatibilityManager(
    modId = "legendarysurvivaloverhaul",
    mixinPackagePrefix = "me.muksc.tacztweaks.mixin.feature.general.compatibility.lso"
) {
    @JvmStatic
    fun <T> wrapOperation(bullet: EntityKineticBullet, block: () -> T): T {
        val ext = RayTracingBullet.of(bullet)
        val originalPosition = bullet.position()
        try {
            bullet.setPos(ext.`tacztweaks$getCurrentHitPosition`())
            return block()
        } finally {
            bullet.setPos(originalPosition)
        }
    }
}