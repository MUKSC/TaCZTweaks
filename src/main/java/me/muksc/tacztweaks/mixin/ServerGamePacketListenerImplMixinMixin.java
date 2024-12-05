package me.muksc.tacztweaks.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.api.entity.IGunOperator;
import me.muksc.tacztweaks.Config;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1500, remap = false)
public abstract class ServerGamePacketListenerImplMixinMixin {
    @TargetHandler(
        mixin = "com.tacz.guns.mixin.common.ServerGamePacketListenerImplMixin",
        name = "cancelSprintCommand"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/entity/ReloadState$StateType;isReloading()Z"))
    private boolean sprintWhileReloading(boolean original) {
        if (!Config.sprintWhileReloading) return original;
        return false;
    }

    @TargetHandler(
        mixin = "com.tacz.guns.mixin.common.ServerGamePacketListenerImplMixin",
        name = "cancelSprintCommand"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/entity/IGunOperator;getSynIsAiming()Z"))
    private boolean preventSprintingWhileShootCooldown(boolean original, @Local IGunOperator operator) {
        if (Config.shootWhileSprinting != Config.EShootWhileSprinting.STOP_SPRINTING) return original;
        return original || operator.getSynShootCoolDown() > 0L;
    }
}
