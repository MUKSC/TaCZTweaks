package me.muksc.tacztweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.entity.shooter.LivingEntityAim;
import me.muksc.tacztweaks.Config;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LivingEntityAim.class, remap = false)
public abstract class LivingEntityAimMixin {
    @ModifyExpressionValue(method = "tickSprint", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/entity/ReloadState$StateType;isReloading()Z"))
    private boolean sprintWhileReloading(boolean original) {
        if (!Config.Gun.INSTANCE.sprintWhileReloading()) return original;
        return false;
    }

    @ModifyExpressionValue(method = "tickSprint", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/tacz/guns/entity/shooter/ShooterDataHolder;isAiming:Z"))
    private boolean preventSprintingWhileShootCooldown(boolean original, @Local IGunOperator operator) {
        if (Config.Gun.INSTANCE.shootWhileSprinting()) return original;
        return original || operator.getSynShootCoolDown() > 0L;
    }
}
