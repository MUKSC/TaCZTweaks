package me.muksc.tacztweaks.mixin.compat.soundphysics;

import com.mojang.blaze3d.audio.Channel;
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsConditionalSoundInstance;
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsCompat;
import me.muksc.tacztweaks.data.BulletSounds;
import me.muksc.tacztweaks.data.BulletSoundsManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Channel.class)
public abstract class ChannelMixin {
    @Shadow public abstract void destroy();

    @Inject(method = "play", at = @At(value = "INVOKE", target = "Lorg/lwjgl/openal/AL10;alSourcePlay(I)V", remap = false), cancellable = true)
    private void tacztweaks$play$cancelPlay(CallbackInfo ci) {
        try {
            SoundPhysicsConditionalSoundInstance sound = SoundPhysicsCompat.INSTANCE.getCurrentSound();
            Float airspace = SoundPhysicsCompat.INSTANCE.getCurrentAirspace();
            Double occlusion = SoundPhysicsCompat.INSTANCE.getCurrentOcclusionAccumulation();
            Float reflectivity = SoundPhysicsCompat.INSTANCE.getCurrentReflectivity();
            if (sound == null || airspace == null || occlusion == null || reflectivity == null) return;
            reflectivity /= SoundPhysicsCompat.INSTANCE.getReflectivityDivider();
            if (sound.canPlayAtAirspace(airspace) && sound.canPlayAtOcclusion(occlusion) && sound.canPlayAtReflectivity(reflectivity)) return;
            ci.cancel();
            destroy();
        } finally {
            SoundPhysicsCompat.INSTANCE.setCurrentSound(null);
            SoundPhysicsCompat.INSTANCE.setCurrentAirspace(null);
            SoundPhysicsCompat.INSTANCE.setCurrentOcclusionAccumulation(null);
            SoundPhysicsCompat.INSTANCE.setCurrentReflectivity(null);
        }
    }
}
