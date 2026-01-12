package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.client.sound.GunSoundInstance;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

@Mixin(value = GunSoundInstance.class, remap = false)
public abstract class GunSoundInstanceMixin {
    @WrapOperation(method = "getSoundBuffer", at = @At(value = "INVOKE", target = "Ljavax/sound/sampled/AudioFormat;getFrameSize()I"))
    private int tacztweaks$getSoundBuffer$monoFrameSize(AudioFormat instance, Operation<Integer> original) {
        if (!Config.Tweaks.INSTANCE.betterMonoConversion()) return original.call(instance);
        if (!tacztweaks$isBetterMonoCompatible(instance)) return original.call(instance);
        return instance.getSampleSizeInBits() / 8;
    }

    @ModifyExpressionValue(method = "getSoundBuffer", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/resource/manager/SoundAssetsManager$SoundData;byteBuffer()Ljava/nio/ByteBuffer;", ordinal = 0))
    private ByteBuffer tacztweaks$getSoundBuffer$betterMono(ByteBuffer original, @Local(ordinal = 0) AudioFormat rawFormat) {
        if (!Config.Tweaks.INSTANCE.betterMonoConversion()) return original;
        if (!tacztweaks$isBetterMonoCompatible(rawFormat)) return original;

        int sampleSizeInBits = rawFormat.getSampleSizeInBits();
        ByteBuffer monoBuffer = ByteBuffer.allocateDirect(original.remaining() / 2);
        monoBuffer.order(original.order());
        if (sampleSizeInBits == 16) {
            ShortBuffer stereoShortBuffer = original.asShortBuffer();
            while (stereoShortBuffer.hasRemaining()) {
                short left = stereoShortBuffer.get();
                short right = stereoShortBuffer.get();
                short mono = (short) ((left + right) / 2);
                monoBuffer.putShort(mono);
            }
        } else if (sampleSizeInBits == 8) {
            while (original.hasRemaining()) {
                byte left = original.get();
                byte right = original.get();
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
    private static boolean tacztweaks$isBetterMonoCompatible(AudioFormat format) {
        int sampleSizeInBits = format.getSampleSizeInBits();
        return sampleSizeInBits == 16 || sampleSizeInBits == 8;
    }
}
