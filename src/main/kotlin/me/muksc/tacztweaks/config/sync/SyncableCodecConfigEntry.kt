package me.muksc.tacztweaks.config.sync

import dev.isxander.yacl3.config.v3.ConfigEntry
import dev.isxander.yacl3.config.v3.EntryAddable
import net.minecraft.network.FriendlyByteBuf
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

@Suppress("UnstableApiUsage")
class SyncableCodecConfigEntry<T>(
    private val base: ConfigEntry<T>,
    private val encoder: (FriendlyByteBuf, value: T) -> Unit,
    private val decoder: (FriendlyByteBuf) -> T
) : ConfigEntry<T> by base, SyncableConfigEntry<T> {
    override var saving = false
    override var override: T? = null
    override val syncedValue: T
        get() = override.takeIf { !saving } ?: get()

    override fun sync(direction: ESyncDirection) {
        (syncedValue as? SyncableEntryAddable)?.sync(direction)
        when (direction) {
            ESyncDirection.NONE,
            ESyncDirection.CLIENT_TO_SERVER -> set(syncedValue)
            ESyncDirection.SERVER_TO_CLIENT -> { /* Nothing */ }
            ESyncDirection.RESET -> override = null
        }
    }

    override fun encode(buf: FriendlyByteBuf) {
        encoder(buf, syncedValue)
    }

    override fun decode(buf: FriendlyByteBuf) {
        override = decoder(buf)
    }

    companion object {
        fun <T> ConfigEntry<T>.toSyncable(encoder: (FriendlyByteBuf, value: T) -> Unit, decoder: (FriendlyByteBuf) -> T): SyncableCodecConfigEntry<T> =
            SyncableCodecConfigEntry(this, encoder, decoder)

        fun <T> PropertyDelegateProvider<EntryAddable, ReadOnlyProperty<EntryAddable, ConfigEntry<T>>>.toSyncable(encoder: (FriendlyByteBuf, value: T) -> Unit, decoder: (FriendlyByteBuf) -> T): PropertyDelegateProvider<EntryAddable, ReadOnlyProperty<EntryAddable, SyncableCodecConfigEntry<T>>> =
            PropertyDelegateProvider { thisRef, property ->
                val entry = this.provideDelegate(thisRef, property).getValue(thisRef, property).toSyncable(encoder, decoder)
                ReadOnlyProperty { _, _ -> entry }
            }
    }
}