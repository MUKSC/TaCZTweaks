package me.muksc.tacztweaks.mixin.feature.disarm;

import com.tacz.guns.client.gameplay.LocalPlayerReload;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerReload.class, remap = false)
public abstract class LocalPlayerReloadMixin {
    @Inject(method = "reload", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$reload$disarm(CallbackInfo ci) {
        if (DisarmManager.shouldDisarm()) ci.cancel();
    }
}