package me.muksc.tacztweaks.registry

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.registry.DeferredHolder
import me.muksc.tacztweaks.core.registry.DeferredRegister
import me.muksc.tacztweaks.core.registry.PlatformRegistries
import me.muksc.tacztweaks.core.registry.wrap
import me.muksc.tacztweaks.effect.FlagStatusEffect
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import java.util.function.Supplier

object ModStatusEffects {
    val REGISTRY: DeferredRegister<MobEffect> = DeferredRegister.create(PlatformRegistries.MOB_EFFECT, TaCZTweaks.MOD_ID)

    @JvmField val DISARM = register("disarm") {
        FlagStatusEffect(MobEffectCategory.HARMFUL, 0x4C46BF)
    }
    @JvmField val ENDLESS_AMMO = register("endless_ammo") {
        FlagStatusEffect(MobEffectCategory.BENEFICIAL, 0xC11B1B)
    }

    //? if fabric
    //@Suppress("RedundantSamConstructor")
    fun <T : MobEffect> register(name: String, block: () -> T): DeferredHolder<MobEffect, T> {
        return REGISTRY.register(name, Supplier {
            block()
        }).wrap()
    }
}