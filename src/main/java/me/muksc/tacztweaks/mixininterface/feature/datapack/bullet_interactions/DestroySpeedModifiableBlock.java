package me.muksc.tacztweaks.mixininterface.feature.datapack.bullet_interactions;

import net.minecraft.world.level.block.state.BlockBehaviour;

public interface DestroySpeedModifiableBlock {
    static DestroySpeedModifiableBlock of(BlockBehaviour instance) {
        return (DestroySpeedModifiableBlock) instance;
    }

    void tacztweaks$setDestroySpeedMultiplier(float multiplier);
}