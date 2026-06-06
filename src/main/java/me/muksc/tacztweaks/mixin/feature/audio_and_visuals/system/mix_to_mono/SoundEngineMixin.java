package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.system.mix_to_mono;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.SoundBuffer;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.feature.audio_and_visuals.system.StereoToMonoMixer;
import me.muksc.tacztweaks.mixin.accessor.SoundBufferAccessor;
import me.muksc.tacztweaks.mixininterface.feature.audio_and_visuals.system.mix_to_mono.MonoObject;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {
    @ModifyExpressionValue(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundBufferLibrary;getCompleteBuffer(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<SoundBuffer> tacztweaks$play$mixToMono(
        CompletableFuture<SoundBuffer> original,
        @Local Sound sound
    ) {
        if (!Config.AudioAndVisuals.System.mixToMono()) return original;
        ResourceLocation id = sound.getPath();
        if (!MonoObject.of(id).tacztweaks$getMono()) return original;
        return original.thenApply(buffer -> {
            SoundBufferAccessor accessor = (SoundBufferAccessor) buffer;
            ByteBuffer data = accessor.getData();
            if (data == null) return buffer;
            AudioFormat format = accessor.getFormat();
            ByteBuffer monoData = StereoToMonoMixer.process(id, data, format);
            if (monoData == null) return buffer;
            return new SoundBuffer(monoData, new AudioFormat(
                format.getEncoding(), format.getSampleRate(), format.getSampleSizeInBits(),
                1, format.getFrameSize() / 2,
                format.getFrameRate(), format.isBigEndian(), format.properties()
            ));
        });
    }
}