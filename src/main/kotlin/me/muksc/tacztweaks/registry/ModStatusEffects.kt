package me.muksc.tacztweaks.registry

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.effect.EndlessAmmoStatusEffect
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModStatusEffects {
    private val REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TaCZTweaks.MOD_ID)

    @JvmField
    val ENDLESS_AMMO: RegistryObject<EndlessAmmoStatusEffect> = REGISTRY.register("endless_ammo", ::EndlessAmmoStatusEffect)

    fun register(bus: IEventBus) {
        REGISTRY.register(bus)
    }
}