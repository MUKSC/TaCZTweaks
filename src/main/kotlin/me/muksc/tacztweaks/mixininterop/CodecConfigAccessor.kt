package me.muksc.tacztweaks.mixininterop

import dev.isxander.yacl3.config.v3.ReadonlyConfigEntry
import me.muksc.tacztweaks.mixin.accessor.CodecConfigAccessor

inline val CodecConfigAccessor.entries: List<ReadonlyConfigEntry<*>>
    get() = `tacztweaks$getEntries`()