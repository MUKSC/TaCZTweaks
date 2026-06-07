package me.muksc.tacztweaks.mixininterop

import me.muksc.tacztweaks.mixin.accessor.BlockInputAccessor
import net.minecraft.nbt.CompoundTag

inline val BlockInputAccessor.tag: CompoundTag?
    get() = `tacztweaks$getTag`()