package me.muksc.tacztweaks.mixininterface.feature.datapack.shield;

import me.muksc.tacztweaks.feature.datapack.shield.CustomShieldResult;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface CustomShieldEntity {
    static CustomShieldEntity of(LivingEntity instance) {
        return (CustomShieldEntity) instance;
    }

    @Nullable CustomShieldResult tacztweaks$getShieldResult();

    void tacztweaks$setShieldResult(CustomShieldResult result);
}