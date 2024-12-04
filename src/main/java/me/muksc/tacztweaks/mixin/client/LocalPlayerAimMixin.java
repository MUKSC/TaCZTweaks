package me.muksc.tacztweaks.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.client.gameplay.LocalPlayerAim;
import me.muksc.tacztweaks.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LocalPlayerAim.class)
public abstract class LocalPlayerAimMixin {
    @ModifyExpressionValue(method = "cancelSprint", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/entity/ReloadState$StateType;isReloading()Z", remap = false), remap = false)
    private boolean sprintWhileReloading(boolean original) {
        if (!Config.sprintWhileReloading) return original;
        return false;
    }

    @ModifyVariable(method = "cancelSprint", at = @At("LOAD"), ordinal = 1, remap = false)
    private boolean stopSprintingOnShot(boolean isAiming, @Local IGunOperator operator) {
        if (Config.shootWhileSprinting != Config.EShootWhileSprinting.STOP_SPRINTING) return isAiming;
        return isAiming || operator.getSynShootCoolDown() > 0L;
    }
}
