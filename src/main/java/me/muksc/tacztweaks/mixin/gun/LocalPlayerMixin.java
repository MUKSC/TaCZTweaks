package me.muksc.tacztweaks.mixin.gun;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.client.input.TiltGunKey;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @ModifyReturnValue(method = "canStartSprinting", at = @At("RETURN"))
    private boolean tacztweaks$canStartSprinting$tiltGunKeyCancelsSprint(boolean original) {
        if (!Config.Gun.INSTANCE.tiltGunKeyCancelsSprint()) return original;
        var instance = LocalPlayer.class.cast(this);
        return original && !TiltGunKey.isActive(instance);
    }
}
