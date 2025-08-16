package me.muksc.tacztweaks.mixin.compat.soundphysics;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
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
    private static float tacztweaks$evaluateEnvironment$setCurrentAirspace(float original) {
        SoundPhysicsCompat.INSTANCE.setCurrentAirspace(original);
        return original;
    }

    @ModifyExpressionValue(method = "evaluateEnvironment", at = @At(value = "INVOKE", target = "Lcom/sonicether/soundphysics/SoundPhysics;calculateOcclusion(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/sounds/SoundSource;Ljava/lang/String;)D"))
    private static double tacztweaks$evaluateEnvironment$setCurrentOcclusionAccumulation(double original) {
        SoundPhysicsCompat.INSTANCE.setCurrentOcclusionAccumulation(original);
        return original;
    }

    @Definition(id = "bounceReflectivityRatio", local = @Local(type = float[].class))
    @Expression("? = ? * @(bounceReflectivityRatio[1])")
    @ModifyExpressionValue(method = "evaluateEnvironment", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static float tacztweaks$evaluateEnvironment$setCurrentReflectivity(float original) {
        SoundPhysicsCompat.INSTANCE.setCurrentReflectivity(original);
        SoundPhysicsCompat.INSTANCE.setReflectivityDivider(1);
        return original;
    }

    @Definition(id = "pow", method = "Ljava/lang/Math;pow(DD)D")
    @Definition(id = "bounceReflectivityRatio", local = @Local(type = float[].class))
    @Expression("? = ? * (float) pow((double) @(bounceReflectivityRatio[2]), 3.0)")
    @ModifyExpressionValue(method = "evaluateEnvironment", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static float tacztweaks$evaluateEnvironment$setCurrentReflectivity2(float original) {
        SoundPhysicsCompat.INSTANCE.setCurrentReflectivity(SoundPhysicsCompat.INSTANCE.getCurrentReflectivity() + original);
        SoundPhysicsCompat.INSTANCE.setReflectivityDivider(2);
        return original;
    }

    @Definition(id = "pow", method = "Ljava/lang/Math;pow(DD)D")
    @Definition(id = "bounceReflectivityRatio", local = @Local(type = float[].class))
    @Expression("? = ? * (float) pow((double) @(bounceReflectivityRatio[3]), 4.0)")
    @ModifyExpressionValue(method = "evaluateEnvironment", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static float tacztweaks$evaluateEnvironment$setCurrentReflectivity3(float original) {
        SoundPhysicsCompat.INSTANCE.setCurrentReflectivity(SoundPhysicsCompat.INSTANCE.getCurrentReflectivity() + original);
        SoundPhysicsCompat.INSTANCE.setReflectivityDivider(3);
        return original;
    }
}
