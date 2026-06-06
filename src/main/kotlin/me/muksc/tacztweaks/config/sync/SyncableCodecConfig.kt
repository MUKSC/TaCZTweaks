package me.muksc.tacztweaks.config.sync

import com.mojang.serialization.Codec
import dev.isxander.yacl3.config.v3.CodecConfig
import io.netty.buffer.ByteBuf
import me.muksc.tacztweaks.config.sync.SyncableCodecConfigEntry.Companion.toSyncable
import me.muksc.tacztweaks.core.codec.StreamCodec

abstract class SyncableCodecConfig<S : SyncableCodecConfig<S>> : CodecConfig<S>(), SyncableEntryAddable {
    private val _syncableEntries = mutableListOf<SyncableConfigEntry<*>>()
    override val syncableEntries: List<SyncableConfigEntry<*>> get() = _syncableEntries

    override fun <T : Any> registerSyncable(
        fieldName: String,
        default: T,
        codec: Codec<T>,
        streamCodec: StreamCodec<ByteBuf, T>
    ): SyncableConfigEntry<T> {
        val entry = register(fieldName, default, codec)
            .toSyncable(streamCodec)
        _syncableEntries.add(entry)
        return entry
    }
}