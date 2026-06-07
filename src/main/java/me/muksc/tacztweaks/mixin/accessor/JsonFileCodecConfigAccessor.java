package me.muksc.tacztweaks.mixin.accessor;

import com.google.gson.Gson;
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;

@Mixin(value = JsonFileCodecConfig.class, remap = false)
public interface JsonFileCodecConfigAccessor {
    @Accessor("configPath")
    Path tacztweaks$getConfigPath();

    @Accessor
    Gson getGson();
}