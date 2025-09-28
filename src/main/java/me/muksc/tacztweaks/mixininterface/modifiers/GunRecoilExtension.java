package me.muksc.tacztweaks.mixininterface.modifiers;

import java.util.function.Function;

public interface GunRecoilExtension {
    void tacztweaks$setModifier(Function<Double, Double> modifier);
}
