package me.muksc.tacztweaks.mixin.compat.soundphysics;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.Channel;
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsCompat;
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsTriggerSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SoundEngine.class, remap = false)
public abstract class SoundEngineMixin {
    @WrapOperation(method = "lambda$play$6", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/Channel;play()V", remap = true))
    private void tacztweaks$play$runProcessing(Channel instance, Operation<Void> original, @Local(argsOnly = true) SoundInstance soundinstance) {
        if (!(soundinstance instanceof SoundPhysicsTriggerSoundInstance sound)) {
            original.call(instance);
            return;
        }
        SoundPhysicsCompat.INSTANCE.runProcessing(sound, () -> original.call(instance));
    }

    @WrapOperation(method = "lambda$play$8", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/Channel;play()V", remap = true))
    private void tacztweaks$play$runProcessing2(Channel instance, Operation<Void> original, @Local(argsOnly = true) SoundInstance soundinstance) {
        if (!(soundinstance instanceof SoundPhysicsTriggerSoundInstance sound)) {
            original.call(instance);
            return;
        }
        SoundPhysicsCompat.INSTANCE.runProcessing(sound, () -> original.call(instance));
    }
}
