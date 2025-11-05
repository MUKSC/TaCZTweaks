package me.muksc.tacztweaks.mixin.gun;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.client.animation.statemachine.GunAnimationStateContext;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.client.input.TiltGunKey;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GunAnimationStateContext.class, remap = false)
public abstract class GunAnimationStateContextMixin {
    @WrapOperation(method = "lambda$shouldSlide$18", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isCrouching()Z", remap = true))
    private boolean tacztweaks$shouldSlide$tiltGunKey(Entity instance, Operation<Boolean> original) {
        return original.call(instance) || ((Config.Gun.INSTANCE.tiltGunKeyCancelsSprint() || !instance.isSprinting()) && TiltGunKey.KEY.isDown());
    }
}
