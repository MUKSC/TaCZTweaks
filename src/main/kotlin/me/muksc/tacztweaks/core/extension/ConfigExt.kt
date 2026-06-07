package me.muksc.tacztweaks.core.extension

import dev.isxander.yacl3.config.v3.CodecConfig
import dev.isxander.yacl3.config.v3.ConfigEntry
import dev.isxander.yacl3.config.v3.default
import dev.isxander.yacl3.config.v3.value
import me.muksc.tacztweaks.mixin.accessor.CodecConfigAccessor
import me.muksc.tacztweaks.mixininterop.entries

fun ConfigEntry<*>.isDefault(): Boolean = value == default

fun CodecConfig<*>.isDefault(): Boolean {
    val accessor = this as CodecConfigAccessor
    return accessor.entries.all {
        val entry = it as? ConfigEntry<*> ?: return true
        entry.isDefault()
    }
}