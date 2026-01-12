package me.muksc.tacztweaks.config.sync

import com.mojang.serialization.Codec
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig
import me.muksc.tacztweaks.config.sync.SyncableCodecConfigEntry.Companion.toSyncable
import net.minecraft.network.FriendlyByteBuf
import java.nio.file.Path

@Suppress("UnstableApiUsage")
abstract class SyncableJsonFileCodecConfig<T : SyncableJsonFileCodecConfig<T>>(
    configPath: Path
) : JsonFileCodecConfig<T>(configPath), SyncableEntryAddable {
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