package me.muksc.tacztweaks.config

import com.mojang.serialization.Codec
import dev.isxander.yacl3.config.v3.EntryAddable
import net.minecraft.network.FriendlyByteBuf
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

@Suppress("UnstableApiUsage")
interface SyncableEntryAddable : EntryAddable {
    val syncableEntries: List<SyncableConfigEntry<*>>

    fun <T> registerSyncable(
        fieldName: String,
        default: T,
        codec: Codec<T>,
        encoder: (FriendlyByteBuf, value: T) -> Unit,
        decoder: (FriendlyByteBuf) -> T
    ): SyncableConfigEntry<T>

    fun <T : SyncableCodecConfig<T>> registerSyncable(
        fieldName: String,
        syncable: T
    ): SyncableConfigEntry<T> =
        registerSyncable(fieldName, syncable, syncable, encoder = { buf, value ->
            value.encode(buf)
        }, decoder = { buf ->
            syncable.apply { decode(buf) }
        })

    fun <T> registerSyncable(
        default: T,
        codec: Codec<T>,
        encoder: (FriendlyByteBuf, value: T) -> Unit,
        decoder: (FriendlyByteBuf) -> T
    ): PropertyDelegateProvider<SyncableEntryAddable, ReadOnlyProperty<SyncableEntryAddable, SyncableConfigEntry<T>>> =
        PropertyDelegateProvider { thisRef, property->
            val entry = thisRef.registerSyncable(property.name, default, codec, encoder, decoder)
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

    fun encode(buf: FriendlyByteBuf) {
        for (entry in syncableEntries) {
            entry.encode(buf)
        }
    }

    fun decode(buf: FriendlyByteBuf) {
        for (entry in syncableEntries) {
            entry.decode(buf)
        }
    }
}