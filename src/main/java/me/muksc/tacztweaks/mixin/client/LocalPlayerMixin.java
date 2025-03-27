package me.muksc.tacztweaks.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import me.muksc.tacztweaks.Config;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @ModifyReturnValue(method = "canStartSprinting", at = @At("RETURN"))
    private boolean tacztweaks$canStartSprinting$preventSprintingWhileShootCooldown(boolean original) {
        if (Config.Gun.INSTANCE.shootWhileSprinting()) return original;
        IClientPlayerGunOperator operator = IClientPlayerGunOperator.fromLocalPlayer(LocalPlayer.class.cast(this));
        return original && operator.getClientShootCoolDown() <= 0L;
    }
}
