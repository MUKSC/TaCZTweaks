package me.muksc.tacztweaks.mixininterop

import me.muksc.tacztweaks.mixin.accessor.JsonFileCodecConfigAccessor
import java.nio.file.Path

inline val JsonFileCodecConfigAccessor.configPath: Path
    get() = `tacztweaks$getConfigPath`()