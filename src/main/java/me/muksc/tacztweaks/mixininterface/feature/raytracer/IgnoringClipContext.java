package me.muksc.tacztweaks.mixininterface.feature.raytracer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;

import java.util.Collection;

public interface IgnoringClipContext {
    static IgnoringClipContext of(ClipContext instance) {
        return (IgnoringClipContext) instance;
    }

    void tacztweaks$setIgnores(Collection<BlockPos> ignores);
}