package me.muksc.tacztweaks.config.sync

import com.mojang.serialization.Codec
import dev.isxander.yacl3.config.v3.CodecConfig
import me.muksc.tacztweaks.config.sync.SyncableCodecConfigEntry.Companion.toSyncable
import net.minecraft.network.FriendlyByteBuf

@Suppress("UnstableApiUsage")
abstract class SyncableCodecConfig<S : SyncableCodecConfig<S>> : CodecConfig<S>(), SyncableEntryAddable {
    private val _syncableEntries = mutableListOf<SyncableConfigEntry<*>>()
    override val syncableEntries: List<SyncableConfigEntry<*>>
        get() = _syncableEntries

    override fun <T> registerSyncable(
        fieldName: String,
        default: T,
        codec: Codec<T>,
        encoder: (FriendlyByteBuf, T) -> Unit,
        decoder: (FriendlyByteBuf) -> T,
    ): SyncableConfigEntry<T> {
        val entry = register(fieldName, default, codec)
            .toSyncable(encoder, decoder)
        _syncableEntries.add(entry)
        return entry
    }
}