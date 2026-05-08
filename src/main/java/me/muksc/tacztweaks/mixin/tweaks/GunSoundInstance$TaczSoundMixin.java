package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.muksc.tacztweaks.mixininterface.tweaks.MonoTaczSound;
import me.muksc.tacztweaks.mixininterface.tweaks.TaCZResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.tacz.guns.client.sound.GunSoundInstance$TaczSound", remap = false)
public abstract class GunSoundInstance$TaczSoundMixin implements MonoTaczSound {
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

    @ModifyReturnValue(method = "getPath", at = @At("RETURN"), remap = true)
    private ResourceLocation tacztweaks(ResourceLocation original) {
        TaCZResourceLocation id = (TaCZResourceLocation) original;
        id.tacztweaks$setMonoAudio(tacztweaks$mono);
        return original;
    }
}