package me.muksc.tacztweaks.mixin.feature.disarm;

import com.tacz.guns.client.gameplay.LocalPlayerAim;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerAim.class, remap = false)
public abstract class LocalPlayerAimMixin {
    @Inject(method = "aim", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$aim$disarm(boolean isAim, CallbackInfo ci) {
        if (DisarmManager.shouldDisarm()) ci.cancel();
    }
}