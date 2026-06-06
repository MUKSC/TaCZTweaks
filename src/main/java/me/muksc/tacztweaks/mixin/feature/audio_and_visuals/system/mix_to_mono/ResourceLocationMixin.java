package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.system.mix_to_mono;

import me.muksc.tacztweaks.mixininterface.feature.audio_and_visuals.system.mix_to_mono.MonoObject;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin implements MonoObject {
    @Unique
    private boolean tacztweaks$mono = false;

    @Override
    public boolean tacztweaks$getMono() {
        return tacztweaks$mono;
    }

    @Override
    public void tacztweaks$setMono(boolean mono) {
        tacztweaks$mono = mono;
    }
}