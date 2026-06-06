package me.muksc.tacztweaks.config.sync

import com.mojang.serialization.Codec
import dev.isxander.yacl3.config.v3.EntryAddable
import io.netty.buffer.ByteBuf
import me.muksc.tacztweaks.core.codec.StreamCodec
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

interface SyncableEntryAddable : EntryAddable {
    val syncableEntries: List<SyncableConfigEntry<*>>

    fun <T : Any> registerSyncable(
        fieldName: String,
        default: T,
        codec: Codec<T>,
        streamCodec: StreamCodec<ByteBuf, T>
    ): SyncableConfigEntry<T>

    fun <T : SyncableCodecConfig<T>> registerSyncable(
        fieldName: String,
        syncable: T
    ): SyncableConfigEntry<T> =
        registerSyncable(fieldName, syncable, syncable, StreamCodec.of(
            { buf, value -> value.encode(buf) },
            { buf -> syncable.apply { decode(buf) } }
        ))

    fun <T : Any> registerSyncable(
        default: T,
        codec: Codec<T>,
        streamCodec: StreamCodec<ByteBuf, T>
    ): PropertyDelegateProvider<SyncableEntryAddable, ReadOnlyProperty<SyncableEntryAddable, SyncableConfigEntry<T>>> =
        PropertyDelegateProvider { thisRef, property->
            val entry = thisRef.registerSyncable(property.name, default, codec, streamCodec)
            ReadOnlyProperty { _, _ -> entry }
        }

    fun <T : SyncableCodecConfig<T>> registerSyncable(
        syncable: T
    ): PropertyDelegateProvider<SyncableEntryAddable, ReadOnlyProperty<SyncableEntryAddable, SyncableConfigEntry<T>>> =
        PropertyDelegateProvider { thisRef, property ->
            val entry = thisRef.registerSyncable(property.name, syncable)
            ReadOnlyProperty { _, _ -> entry }
        }

    fun runAsSaving(save: () -> Unit) {
        try {
            for (entry in syncableEntries) {
                entry.saving = true
            }
            save()
        } finally {
            for (entry in syncableEntries) {
                entry.saving = false
            }
        }
    }

    fun sync(direction: ESyncDirection) {
        for (entry in syncableEntries) {
            entry.sync(direction)
        }
    }

    fun encode(buf: ByteBuf) {
        for (entry in syncableEntries) {
            entry.encode(buf)
        }
    }

    fun decode(buf: ByteBuf) {
        for (entry in syncableEntries) {
            entry.decode(buf)
        }
    }
}