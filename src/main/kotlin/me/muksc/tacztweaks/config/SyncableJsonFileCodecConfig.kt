package me.muksc.tacztweaks.config

import dev.isxander.yacl3.config.v3.JsonFileCodecConfig
import java.nio.file.Path

@Suppress("UnstableApiUsage")
abstract class SyncableJsonFileCodecConfig<T : SyncableJsonFileCodecConfig<T>>(
    configPath: Path
) : JsonFileCodecConfig<T>(configPath), SyncableEntryAddable by SyncableEntryAddableDelegate()