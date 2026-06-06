package me.muksc.tacztweaks.fabric.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity

object ShieldBlockEvent {
    @JvmField
    val CALLBACK: Event<Callback> = EventFactory.createArrayBacked(Callback::class.java) { callbacks -> Callback { entity, source, amount ->
        for (callback in callbacks) {
            callback.onShieldBlock(entity, source, amount)
        }
    } }

    fun interface Callback {
        fun onShieldBlock(entity: LivingEntity, source: DamageSource, amount: Float)
    }
}