package me.muksc.tacztweaks.effect

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity

class EndlessAmmoStatusEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0) {
    override fun applyEffectTick(pLivingEntity: LivingEntity, pAmplifier: Int) = Unit

    override fun isDurationEffectTick(pDuration: Int, pAmplifier: Int): Boolean = false
}