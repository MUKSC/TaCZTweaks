package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.system.mix_to_mono;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.muksc.tacztweaks.mixininterface.feature.audio_and_visuals.system.mix_to_mono.MonoObject;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.tacz.guns.client.sound.GunSoundInstance$TaczSound")
public abstract class GunSoundInstance_TaczSoundMixin implements MonoObject {
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

    @ModifyReturnValue(method = "getPath", at = @At("RETURN"))
    private ResourceLocation tacztweaks$mixToMono$setMono(ResourceLocation original) {
        MonoObject object = MonoObject.of(original);
        object.tacztweaks$setMono(tacztweaks$mono);
        return original;
    }
}