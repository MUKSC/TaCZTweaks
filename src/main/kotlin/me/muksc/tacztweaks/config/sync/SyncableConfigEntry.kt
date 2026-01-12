package me.muksc.tacztweaks.config.sync

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.config.v3.ConfigEntry
import dev.isxander.yacl3.config.v3.default
import net.minecraft.network.FriendlyByteBuf

@Suppress("UnstableApiUsage")
interface SyncableConfigEntry<T> : ConfigEntry<T> {
    var saving: Boolean
    var override: T?
    val syncedValue: T

    fun asSyncedBinding(): Binding<T> =
        Binding.generic(default, ::syncedValue, ::override::set)

    fun sync(direction: ESyncDirection)

    fun encode(buf: FriendlyByteBuf)

    fun decode(buf: FriendlyByteBuf)
}