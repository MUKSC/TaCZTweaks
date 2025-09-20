package me.muksc.tacztweaks.mixin.compat.soundphysics;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.sonicether.soundphysics.ReflectedAudio;
import com.sonicether.soundphysics.SoundPhysics;
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
}
