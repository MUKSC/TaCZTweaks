package me.muksc.tacztweaks.mixin.gun.movement;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.entity.shooter.LivingEntityAim;
import me.muksc.tacztweaks.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LivingEntityAim.class, remap = false)
public abstract class LivingEntityAimMixin {
    @ModifyExpressionValue(method = "tickSprint", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/entity/ReloadState$StateType;isReloading()Z"))
    private boolean tacztweaks$tickSprint$sprintWhileReloading(boolean original) {
        if (!Config.Gun.INSTANCE.sprintWhileReloading()) return original;
        return false;
    }
}
