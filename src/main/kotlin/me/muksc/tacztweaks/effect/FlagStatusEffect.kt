package me.muksc.tacztweaks.effect

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity

//? if forge {
import net.minecraft.world.item.ItemStack
//?} else if neoforge {
/*import net.minecraft.world.effect.MobEffectInstance
import net.neoforged.neoforge.common.EffectCure
*///?}

class FlagStatusEffect(category: MobEffectCategory, color: Int) : MobEffect(category, color) {
    //? if <1.20.5 {
    override fun applyEffectTick(livingEntity: LivingEntity, amplifier: Int) = Unit

    override fun isDurationEffectTick(duration: Int, amplifier: Int): Boolean = false
    //?} else {
    /*override fun applyEffectTick(livingEntity: LivingEntity, amplifier: Int): Boolean = false
    *///?}

    //? forge {
    override fun getCurativeItems(): List<ItemStack> = emptyList()
    //?} else if neoforge {
    /*override fun fillEffectCures(cures: Set<EffectCure>, effectInstance: MobEffectInstance) = Unit
    *///?}
}