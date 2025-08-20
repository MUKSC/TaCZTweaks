package me.muksc.tacztweaks.mixin.compat.soundphysics;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.sonicether.soundphysics.ReflectedAudio;
import com.sonicether.soundphysics.SoundPhysics;
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsCompat;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SoundPhysics.class, remap = false)
public abstract class SoundPhysicsMixin {
    @Definition(id = "audioDirection", local = @Local(type = ReflectedAudio.class))
    @Definition(id = "getSharedAirspaces", method = "Lcom/sonicether/soundphysics/ReflectedAudio;getSharedAirspaces()I")
    @Expression("(float) audioDirection.getSharedAirspaces() * 64.0 * ?")
    @ModifyExpressionValue(method = "evaluateEnvironment", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static float tacztweaks$evaluateEnvironment$setAirspace(float original) {
        SoundPhysicsCompat.ProcessingSound sound = SoundPhysicsCompat.INSTANCE.getProcessingSound();
        if (sound == null) return original;
        sound.setAirspace(original);
        return original;
    }

    @ModifyExpressionValue(method = "evaluateEnvironment", at = @At(value = "INVOKE", target = "Lcom/sonicether/soundphysics/SoundPhysics;calculateOcclusion(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/sounds/SoundSource;Ljava/lang/String;)D"))
    private static double tacztweaks$evaluateEnvironment$setOcclusionAccumulation(double original) {
        SoundPhysicsCompat.ProcessingSound sound = SoundPhysicsCompat.INSTANCE.getProcessingSound();
        if (sound == null) return original;
        sound.setOcclusionAccumulation(original);
        return original;
    }

    @Definition(id = "bounceReflectivityRatio", local = @Local(type = float[].class))
    @Expression("? = ? * @(bounceReflectivityRatio[1])")
    @ModifyExpressionValue(method = "evaluateEnvironment", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static float tacztweaks$evaluateEnvironment$setReflectivity(float original) {
        SoundPhysicsCompat.ProcessingSound sound = SoundPhysicsCompat.INSTANCE.getProcessingSound();
        if (sound == null) return original;
        sound.setReflectivity(original);
        sound.setReflectivityDivider(1);
        return original;
    }

    @Definition(id = "pow", method = "Ljava/lang/Math;pow(DD)D")
    @Definition(id = "bounceReflectivityRatio", local = @Local(type = float[].class))
    @Expression("? = ? * (float) pow((double) @(bounceReflectivityRatio[2]), 3.0)")
    @ModifyExpressionValue(method = "evaluateEnvironment", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static float tacztweaks$evaluateEnvironment$setReflectivity2(float original) {
        SoundPhysicsCompat.ProcessingSound sound = SoundPhysicsCompat.INSTANCE.getProcessingSound();
        if (sound == null) return original;
        Float reflectivity = sound.getReflectivity();
        if (reflectivity == null) return original;
        sound.setReflectivity(reflectivity + original);
        sound.setReflectivityDivider(2);
        return original;
    }

    @Definition(id = "pow", method = "Ljava/lang/Math;pow(DD)D")
    @Definition(id = "bounceReflectivityRatio", local = @Local(type = float[].class))
    @Expression("? = ? * (float) pow((double) @(bounceReflectivityRatio[3]), 4.0)")
    @ModifyExpressionValue(method = "evaluateEnvironment", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static float tacztweaks$evaluateEnvironment$setReflectivity3(float original) {
        SoundPhysicsCompat.ProcessingSound sound = SoundPhysicsCompat.INSTANCE.getProcessingSound();
        if (sound == null) return original;
        Float reflectivity = sound.getReflectivity();
        if (reflectivity == null) return original;
        sound.setReflectivity(reflectivity + original);
        sound.setReflectivityDivider(3);
        return original;
    }

    @Inject(method = "evaluateEnvironment", at = @At("RETURN"))
    private static void tacztweaks$evaluateEnvironment$onReturn(int sourceID, double posX, double posY, double posZ, SoundSource category, String sound, boolean auxOnly, CallbackInfoReturnable<Vec3> cir) {
        SoundPhysicsCompat.INSTANCE.onSoundEvaluationComplete();
    }
}
