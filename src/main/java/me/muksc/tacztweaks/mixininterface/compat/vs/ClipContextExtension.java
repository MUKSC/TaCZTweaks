package me.muksc.tacztweaks.mixininterface.compat.vs;

import net.minecraft.core.BlockPos;

import java.util.Collection;

public interface ClipContextExtension {
    void tacztweaks$setIgnores(Collection<BlockPos> ignores);
}
