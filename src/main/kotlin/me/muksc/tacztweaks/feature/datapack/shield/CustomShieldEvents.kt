package me.muksc.tacztweaks.feature.datapack.shield

import com.tacz.guns.entity.EntityKineticBullet
import com.tacz.guns.init.ModDamageTypes
import me.muksc.tacztweaks.feature.datapack.legacy.manager.BulletInteractionManager
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity

object CustomShieldEvents {
    @JvmStatic
    fun onDamageBlock(entity: LivingEntity, source: DamageSource, damage: Float): CustomShieldResult {
        if (!source.`is`(ModDamageTypes.BULLETS_TAG)) return CustomShieldResult.Bypass
        val bullet = source.directEntity as? EntityKineticBullet ?: return CustomShieldResult.Bypass
        val location = source.sourcePosition ?: return CustomShieldResult.Bypass

        return BulletInteractionManager.handleShieldInteraction(bullet, location, entity.useItem, damage)
    }
}