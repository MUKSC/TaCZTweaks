package me.muksc.tacztweaks.mixin.gun.movement;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.client.event.TickAnimationEvent;
import me.muksc.tacztweaks.Config;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TickAnimationEvent.class, remap = false)
public abstract class TickAnimationEventMixin {
    @ModifyExpressionValue(method = "lambda$tickAnimation$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSprinting()Z", remap = true))
    private static boolean tacztweaks$tickAnimation$stopSprintAnimationOnShot(boolean original, @Local(argsOnly = true) LocalPlayer player) {
        if (!Config.Gun.INSTANCE.shootWhileSprinting()) return original;
        IClientPlayerGunOperator operator = IClientPlayerGunOperator.fromLocalPlayer(player);
        return original && operator.getClientShootCoolDown() <= 0;
    }
}
