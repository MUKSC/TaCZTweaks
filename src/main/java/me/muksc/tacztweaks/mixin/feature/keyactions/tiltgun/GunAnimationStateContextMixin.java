package me.muksc.tacztweaks.mixin.feature.keyactions.tiltgun;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.animation.statemachine.GunAnimationStateContext;
import me.muksc.tacztweaks.client.input.TiltGunKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GunAnimationStateContext.class, remap = false)
public abstract class GunAnimationStateContextMixin {
    @ModifyExpressionValue(method = "lambda$shouldSlide$18", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isCrouching()Z", remap = true))
    private boolean tacztweaks$shouldSlide$tiltGunKey(boolean original) {
        return original || TiltGunKey.KEY.isDown();
    }
}