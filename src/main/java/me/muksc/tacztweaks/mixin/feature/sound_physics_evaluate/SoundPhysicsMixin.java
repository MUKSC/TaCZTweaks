package me.muksc.tacztweaks.mixin.feature.sound_physics_evaluate;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.sonicether.soundphysics.ReflectedAudio;
import com.sonicether.soundphysics.SoundPhysics;
import me.muksc.tacztweaks.TaCZTweaks;
import me.muksc.tacztweaks.feature.sound_physics_evaluate.SoundPhysicsEvaluationSoundInstance;
import me.muksc.tacztweaks.feature.sound_physics_evaluate.SoundPhysicsManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SoundPhysics.class, remap = false)
public abstract class SoundPhysicsMixin {
    @Definition(id = "audioDirection", local = @Local(type = ReflectedAudio.class, name = "audioDirection"))
    @Definition(id = "getSharedAirspaces", method = "Lcom/sonicether/soundphysics/ReflectedAudio;getSharedAirspaces()I")
    @Expression("(float) audioDirection.getSharedAirspaces() * 64.0 * ?")
    @ModifyExpressionValue(method = "evaluateEnvironment", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static float tacztweaks$evaluateEnvironment$soundPhysicsEvaluate$airspace(
        float original,
        @Share(value = "processing", namespace = TaCZTweaks.MOD_ID) LocalRef<SoundPhysicsEvaluationSoundInstance> processingRef,
        @Share(value = "evaluation", namespace = TaCZTweaks.MOD_ID) LocalRef<SoundPhysicsManager.EvaluationResult> evaluationRef
    ) {
        if (processingRef.get() != null) evaluationRef.set(evaluationRef.get().withAirspace(original));
        return original;
    }

    @Definition(id = "bounceReflectivityRatio", local = @Local(type = float[].class, name = "bounceReflectivityRatio"))
    @Expression("? = ? * @(bounceReflectivityRatio[1])")
    @ModifyExpressionValue(method = "evaluateEnvironment", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static float tacztweaks$evaluateEnvironment$soundPhysicsEvaluate$reflectivity(
        float original,
        @Share(value = "processing", namespace = TaCZTweaks.MOD_ID) LocalRef<SoundPhysicsEvaluationSoundInstance> processingRef,
        @Share(value = "evaluation", namespace = TaCZTweaks.MOD_ID) LocalRef<SoundPhysicsManager.EvaluationResult> evaluationRef
    ) {
        if (processingRef.get() != null) evaluationRef.set(evaluationRef.get().withReflectivity(original));
        return original;
    }
}