package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.OggAudioStream;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.mixininterface.tweaks.TaCZResourceLocation;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

@Mixin(SoundBufferLibrary.class)
public abstract class SoundBufferLibraryMixin {
    @SuppressWarnings("target") // why
    @ModifyArg(method = "lambda$getCompleteBuffer$0(Lnet/minecraft/resources/ResourceLocation;)Lcom/mojang/blaze3d/audio/SoundBuffer;", at = @At(value = "INVOKE", target = "com/mojang/blaze3d/audio/SoundBuffer.<init>(Ljava/nio/ByteBuffer;Ljavax/sound/sampled/AudioFormat;)V"), index = 1)
    private AudioFormat tacztweaks$getCompleteBuffer$monoFrameSize(
        AudioFormat pFormat,
        @Local(argsOnly = true) ResourceLocation id
    ) {
        if (!Config.Tweaks.INSTANCE.betterMonoConversion()) return pFormat;
        if (!tacztweaks$shouldConvertToMono(pFormat, id)) return pFormat;
        return new AudioFormat(
            pFormat.getEncoding(), pFormat.getSampleRate(), pFormat.getSampleSizeInBits(),
            1, pFormat.getFrameSize() / 2,
            pFormat.getFrameRate(), pFormat.isBigEndian(), pFormat.properties()
        );
    }

    @SuppressWarnings("target") // why
    @ModifyArg(method = "lambda$getCompleteBuffer$0(Lnet/minecraft/resources/ResourceLocation;)Lcom/mojang/blaze3d/audio/SoundBuffer;", at = @At(value = "INVOKE", target = "com/mojang/blaze3d/audio/SoundBuffer.<init>(Ljava/nio/ByteBuffer;Ljavax/sound/sampled/AudioFormat;)V"), index = 0)
    private ByteBuffer tacztweaks$getCompleteBuffer$monoBuffer(
        ByteBuffer pData,
        @Local(argsOnly = true) ResourceLocation id,
        @Local OggAudioStream oggaudiostream
    ) {
        if (!Config.Tweaks.INSTANCE.betterMonoConversion()) return pData;
        AudioFormat rawFormat = oggaudiostream.getFormat();
        if (!tacztweaks$shouldConvertToMono(rawFormat, id)) return pData;

        int sampleSizeInBits = rawFormat.getSampleSizeInBits();
        ByteBuffer monoBuffer = ByteBuffer.allocateDirect(pData.remaining() / 2);
        monoBuffer.order(pData.order());
        if (sampleSizeInBits == 16) {
            ShortBuffer stereoShortBuffer = pData.asShortBuffer();
            while (stereoShortBuffer.hasRemaining()) {
                short left = stereoShortBuffer.get();
                short right = stereoShortBuffer.get();
                short mono = (short) ((left + right) / 2);
                monoBuffer.putShort(mono);
            }
        } else if (sampleSizeInBits == 8) {
            while (pData.hasRemaining()) {
                byte left = pData.get();
                byte right = pData.get();
                byte mono = (byte) ((left + right) / 2);
                monoBuffer.put(mono);
            }
        } else {
            throw new AssertionError("Somehow better mono incompatible audio got through: " + sampleSizeInBits);
        }

        monoBuffer.flip();
        return monoBuffer;
    }

    @Unique
    private static boolean tacztweaks$shouldConvertToMono(AudioFormat format, ResourceLocation id) {
        TaCZResourceLocation tacz = (TaCZResourceLocation) id;
        if (!tacz.tacztweaks$getMonoAudio()) return false;
        if (format.getChannels() == 1) return false;
        int sampleSizeInBits = format.getSampleSizeInBits();
        return sampleSizeInBits == 16 || sampleSizeInBits == 8;
    }
}