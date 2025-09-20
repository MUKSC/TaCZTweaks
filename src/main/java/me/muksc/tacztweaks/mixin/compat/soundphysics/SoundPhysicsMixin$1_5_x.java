package me.muksc.tacztweaks.mixin.compat.soundphysics;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.sonicether.soundphysics.SoundPhysics;
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SoundPhysics.class, remap = false)
public abstract class SoundPhysicsMixin$1_5_x {
    @ModifyExpressionValue(method = "evaluateEnvironment", at = @At(value = "INVOKE", target = "Lcom/sonicether/soundphysics/SoundPhysics;calculateOcclusion(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/resources/ResourceLocation;)D"))
    private static double tacztweaks$evaluateEnvironment$setOcclusionAccumulation(double original) {
        SoundPhysicsCompat.ProcessingSound sound = SoundPhysicsCompat.INSTANCE.getProcessingSound();
        if (sound == null) return original;
        sound.setOcclusionAccumulation(original);
        return original;
    }

    @Inject(method = "evaluateEnvironment", at = @At("RETURN"))
    private static void tacztweaks$evaluateEnvironment$onReturn(int sourceID, double posX, double posY, double posZ, SoundSource category, ResourceLocation sound, boolean auxOnly, CallbackInfoReturnable<Vec3> cir) {
        SoundPhysicsCompat.INSTANCE.onSoundEvaluationComplete();
    }
}
