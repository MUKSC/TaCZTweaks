package me.muksc.tacztweaks.mixin.feature.general.fixes.jittery_shooting_animation_fix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.compat.playeranimator.animation.AnimationManager;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AnimationManager.class, remap = false)
public abstract class AnimationManagerMixin {
    @Shadow
    private static void stopAnimation(AbstractClientPlayer player, ResourceLocation dataId, int fadeTime) {
        throw new AssertionError();
    }

    @WrapOperation(method = "lambda$onFire$4", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/compat/playeranimator/animation/AnimationManager;playOnceAnimation(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/tacz/guns/client/resource/GunDisplayInstance;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;)V"))
    private static void tacztweaks$onFire$jitteryShootingAnimationFix(AbstractClientPlayer player, GunDisplayInstance display, ResourceLocation dataId, String animationName, Operation<Void> original) {
        stopAnimation(player, dataId, 0);
        original.call(player, display, dataId, animationName);
    }
}