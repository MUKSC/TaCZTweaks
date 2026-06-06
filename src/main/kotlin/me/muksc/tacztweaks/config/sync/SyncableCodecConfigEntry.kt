package me.muksc.tacztweaks.config.sync

import dev.isxander.yacl3.config.v3.ConfigEntry
import dev.isxander.yacl3.config.v3.EntryAddable
import io.netty.buffer.ByteBuf
import me.muksc.tacztweaks.core.codec.StreamCodec
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

class SyncableCodecConfigEntry<T : Any>(
    private val base: ConfigEntry<T>,
    private val streamCodec: StreamCodec<ByteBuf, T>
) : ConfigEntry<T> by base, SyncableConfigEntry<T> {
    override var saving = false
    override var override: T? = null
    override val syncedValue: T get() = override.takeIf { !saving } ?: get()

    override fun sync(direction: ESyncDirection) {
        (syncedValue as? SyncableEntryAddable)?.sync(direction)
        when (direction) {
            ESyncDirection.NONE,
            ESyncDirection.CLIENT_TO_SERVER -> set(syncedValue)
            ESyncDirection.SERVER_TO_CLIENT -> Unit
            ESyncDirection.RESET -> override = null
        }
    }

    override fun encode(buf: ByteBuf) {
        streamCodec.encode(buf, syncedValue)
    }

    override fun decode(buf: ByteBuf) {
        override = streamCodec.decode(buf)
    }

    companion object {
        fun <T : Any> ConfigEntry<T>.toSyncable(streamCodec: StreamCodec<ByteBuf, T>): SyncableCodecConfigEntry<T> =
            SyncableCodecConfigEntry(this, streamCodec)

        fun <T : Any> PropertyDelegateProvider<EntryAddable, ReadOnlyProperty<EntryAddable, ConfigEntry<T>>>.toSyncable(streamCodec: StreamCodec<ByteBuf, T>): PropertyDelegateProvider<EntryAddable, ReadOnlyProperty<EntryAddable, SyncableCodecConfigEntry<T>>> =
            PropertyDelegateProvider { thisRef, property ->
                val entry = this.provideDelegate(thisRef, property).getValue(thisRef, property).toSyncable(streamCodec)
                ReadOnlyProperty { _, _ -> entry }
            }
    }
}