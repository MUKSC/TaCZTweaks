package me.muksc.tacztweaks.mixin.gun.unload;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.entity.shooter.LivingEntityUnload;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LivingEntityUnload.class, remap = false)
public abstract class LivingEntityUnloadMixin {
    @ModifyExpressionValue(method = "unloadAmmo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSprinting()Z", remap = true))
    private boolean tacztweaks$unloadAmmo$allowUnload(boolean original) {
        return original || !Config.Gun.INSTANCE.allowUnload();
    }
}