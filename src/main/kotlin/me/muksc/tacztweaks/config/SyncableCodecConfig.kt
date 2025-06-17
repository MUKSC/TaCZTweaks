package me.muksc.tacztweaks.config

import dev.isxander.yacl3.config.v3.CodecConfig

@Suppress("UnstableApiUsage")
abstract class SyncableCodecConfig<S : SyncableCodecConfig<S>>
    : CodecConfig<S>(), SyncableEntryAddable by SyncableEntryAddableDelegate()