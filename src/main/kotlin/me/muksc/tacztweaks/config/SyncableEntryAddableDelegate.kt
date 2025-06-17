package me.muksc.tacztweaks.config

import com.mojang.serialization.Codec
import dev.isxander.yacl3.config.v3.EntryAddable
import me.muksc.tacztweaks.config.SyncableCodecConfigEntry.Companion.toSyncable
import net.minecraft.network.FriendlyByteBuf
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

@Suppress("UnstableApiUsage")
class SyncableEntryAddableDelegate : SyncableEntryAddable {
    private val _syncableEntries = mutableListOf<SyncableConfigEntry<*>>()
    override val syncableEntries: List<SyncableConfigEntry<*>>
        get() = _syncableEntries

    override fun <T> registerSyncable(
        default: T,
        codec: Codec<T>,
        encoder: (FriendlyByteBuf, T) -> Unit,
        decoder: (FriendlyByteBuf) -> T
    ): PropertyDelegateProvider<EntryAddable, ReadOnlyProperty<EntryAddable, SyncableConfigEntry<T>>> =
        PropertyDelegateProvider { thisRef, property->
            val entry = thisRef.register(property.name, default, codec)
                .toSyncable(encoder, decoder)
            _syncableEntries.add(entry)
            ReadOnlyProperty { _, _ -> entry }
        }
}