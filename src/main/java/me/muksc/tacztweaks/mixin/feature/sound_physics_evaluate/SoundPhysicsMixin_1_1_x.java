package me.muksc.tacztweaks.mixin.feature.sound_physics_evaluate;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.sonicether.soundphysics.SoundPhysics;
import me.muksc.tacztweaks.TaCZTweaks;
import me.muksc.tacztweaks.feature.sound_physics_evaluate.SoundPhysicsEvaluationSoundInstance;
import me.muksc.tacztweaks.feature.sound_physics_evaluate.SoundPhysicsManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SoundPhysics.class, remap = false)
public abstract class SoundPhysicsMixin_1_1_x {
    @Dynamic
    @ModifyExpressionValue(method = "evaluateEnvironment", at = @At(value = "INVOKE", target = "Lcom/sonicether/soundphysics/SoundPhysics;calculateOcclusion(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/sounds/SoundSource;Ljava/lang/String;)D"))
    private static float tacztweaks$evaluateEnvironment$soundPhysicsEvaluate$occlusionAccumulation(
        float original,
        @Share(value = "processing", namespace = TaCZTweaks.MOD_ID) LocalRef<SoundPhysicsEvaluationSoundInstance> processingRef,
        @Share(value = "evaluation", namespace = TaCZTweaks.MOD_ID) LocalRef<SoundPhysicsManager.EvaluationResult> evaluationRef
    ) {
        if (processingRef.get() != null) evaluationRef.set(evaluationRef.get().withOcclusionAccumulation(original));
        return original;
    }

    @Dynamic
    @Inject(method = "evaluateEnvironment", at = @At("RETURN"))
    private static void tacztweaks$evaluateEnvironment$soundPhysicsEvaluate$onEvaluationComplete(
        int sourceID, double posX, double posY, double posZ, SoundSource category, String sound, boolean auxOnly, CallbackInfoReturnable<Vec3> cir,
        @Share(value = "processing", namespace = TaCZTweaks.MOD_ID) LocalRef<SoundPhysicsEvaluationSoundInstance> processingRef,
        @Share(value = "evaluation", namespace = TaCZTweaks.MOD_ID) LocalRef<SoundPhysicsManager.EvaluationResult> evaluationRef
    ) {
        if (processingRef.get() != null) SoundPhysicsManager.onEvaluationComplete(processingRef.get(), evaluationRef.get());
    }
}