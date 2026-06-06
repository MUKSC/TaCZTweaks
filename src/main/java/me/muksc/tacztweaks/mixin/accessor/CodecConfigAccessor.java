package me.muksc.tacztweaks.mixin.accessor;

import dev.isxander.yacl3.config.v3.CodecConfig;
import dev.isxander.yacl3.config.v3.ReadonlyConfigEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = CodecConfig.class, remap = false)
public interface CodecConfigAccessor {
    @Accessor
    List<ReadonlyConfigEntry<?>> getEntries();
}