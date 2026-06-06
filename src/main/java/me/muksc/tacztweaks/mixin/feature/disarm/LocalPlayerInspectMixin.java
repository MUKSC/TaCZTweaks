package me.muksc.tacztweaks.mixin.feature.disarm;

import com.tacz.guns.client.gameplay.LocalPlayerInspect;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerInspect.class, remap = false)
public abstract class LocalPlayerInspectMixin {
    @Inject(method = "inspect", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$inspect$disarm(CallbackInfo ci) {
        if (DisarmManager.shouldDisarm()) ci.cancel();
    }
}