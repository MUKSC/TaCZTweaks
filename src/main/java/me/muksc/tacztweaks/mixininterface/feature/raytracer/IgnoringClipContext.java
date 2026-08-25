package me.muksc.tacztweaks.mixininterface.feature.raytracer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;

import java.util.Collection;
import java.util.Set;

public interface IgnoringClipContext {
    static IgnoringClipContext of(ClipContext instance) {
        return (IgnoringClipContext) instance;
    }

    Set<BlockPos> tacztweaks$getIgnores();

    void tacztweaks$setIgnores(Collection<BlockPos> ignores);
}