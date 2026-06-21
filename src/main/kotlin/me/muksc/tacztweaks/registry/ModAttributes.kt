package me.muksc.tacztweaks.registry

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.core.attribute.BooleanAttribute
import me.muksc.tacztweaks.core.registry.DeferredHolder
import me.muksc.tacztweaks.core.registry.DeferredRegister
import me.muksc.tacztweaks.core.registry.PlatformRegistries
import me.muksc.tacztweaks.core.registry.wrap
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import java.util.function.Supplier

object ModAttributes {
    val REGISTRY: DeferredRegister<Attribute> = DeferredRegister.create(PlatformRegistries.ATTRIBUTE, TaCZTweaks.MOD_ID)

    @JvmField val DAMAGE = register("stats.damage") { descriptionId ->
        RangedAttribute(descriptionId, 0.0, 0.0, Double.MAX_VALUE).setSyncable(true)
    }

    @JvmField val ENABLED = register("capability.enabled") { descriptionId ->
        BooleanAttribute(descriptionId, true).setSyncable(true)
    }
    @JvmField val SHOOTING = register("capability.shooting") { descriptionId ->
        BooleanAttribute(descriptionId, true).setSyncable(true)
    }
    @JvmField val RELOADING = register("capability.reloading") { descriptionId ->
        BooleanAttribute(descriptionId, true).setSyncable(true)
    }
    @JvmField val REFITTING = register("capability.refitting") { descriptionId ->
        BooleanAttribute(descriptionId, true).setSyncable(true)
    }

    @JvmField val SHOOT_WHILE_SPRINTING = register("handling.shoot_while_sprinting") { descriptionId ->
        BooleanAttribute(descriptionId, Config.Gameplay.Handling::shootWhileSprinting).setSyncable(true)
    }
    @JvmField val SPRINT_WHILE_RELOADING = register("handling.sprint_while_reloading") { descriptionId ->
        BooleanAttribute(descriptionId, Config.Gameplay.Handling::reloadWhileSprinting).setSyncable(true)
    }

    @JvmField val ATTRIBUTES = listOf(
        DAMAGE,
        ENABLED, SHOOTING, RELOADING, REFITTING,
        SHOOT_WHILE_SPRINTING, SPRINT_WHILE_RELOADING
    )

    //? if fabric
    //@Suppress("RedundantSamConstructor")
    fun <T : Attribute> register(name: String, block: (descriptionId: String) -> T): DeferredHolder<Attribute, T> =
        REGISTRY.register(name, Supplier {
            block(TaCZTweaks.translatable("attribute.$name").string)
        }).wrap()
}