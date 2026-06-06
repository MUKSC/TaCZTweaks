package me.muksc.tacztweaks.mixin.feature.keyactions.tiltgun;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.muksc.tacztweaks.client.input.TiltGunKey;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @ModifyReturnValue(method = "canStartSprinting", at = @At("RETURN"))
    private boolean tacztweaks$canStartSprinting$tiltGunKey$cancelSprint(boolean original) {
        if (!Config.KeyActions.TiltGun.cancelSprint()) return original;
        var instance = LocalPlayer.class.cast(this);
        return original && !TiltGunKey.isActive(instance);
    }
}