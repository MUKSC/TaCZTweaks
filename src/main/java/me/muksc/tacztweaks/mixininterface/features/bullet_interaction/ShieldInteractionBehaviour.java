package me.muksc.tacztweaks.mixininterface.features.bullet_interaction;

import java.util.function.Function;

public interface ShieldInteractionBehaviour {
    Function<Integer, Integer> tacztweaks$getCustomShieldDurabilityDamage();

    void tacztweaks$setCustomShieldDurabilityDamage(Function<Integer, Integer> damage);

    Integer tacztweaks$getCustomShieldDisableDuration();

    void tacztweaks$setCustomShieldDisableDuration(Integer duration);
}
