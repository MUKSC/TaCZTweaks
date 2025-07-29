package me.muksc.tacztweaks.mixin.compat.soundphysics;

import com.mojang.blaze3d.audio.Channel;
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsConditionalSoundInstance;
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Channel.class)
public abstract class ChannelMixin {
    @Inject(method = "play", at = @At(value = "INVOKE", target = "Lorg/lwjgl/openal/AL10;alSourcePlay(I)V", remap = false), cancellable = true)
    private void tacztweaks$play$cancelPlay(CallbackInfo ci) {
        try {
            SoundPhysicsConditionalSoundInstance sound = SoundPhysicsCompat.INSTANCE.getCurrentSound();
            Optional<Float> airspace = Optional.ofNullable(SoundPhysicsCompat.INSTANCE.getCurrentAirspace());
            Optional<Double> occlusion = Optional.ofNullable(SoundPhysicsCompat.INSTANCE.getCurrentOcclusionAccumulation());
            if (sound == null || airspace.isEmpty() || occlusion.isEmpty()) return;
            if (sound.canPlayAtAirspace(airspace.get()) && sound.canPlayAtOcclusion(occlusion.get())) return;
            ci.cancel();
        } finally {
            SoundPhysicsCompat.INSTANCE.setCurrentSound(null);
            SoundPhysicsCompat.INSTANCE.setCurrentAirspace(null);
            SoundPhysicsCompat.INSTANCE.setCurrentOcclusionAccumulation(null);
        }
    }
}
