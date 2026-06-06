package me.muksc.tacztweaks.mixin.feature.disarm;

import com.tacz.guns.client.gameplay.LocalPlayerBolt;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerBolt.class, remap = false)
public abstract class LocalPlayerBoltMixin {
    @Inject(method = "bolt", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$bolt$disarm(CallbackInfo ci) {
        if (DisarmManager.shouldDisarm()) ci.cancel();
    }

    @Inject(method = "tickAutoBolt", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$tickAutoBolt$disarm(CallbackInfo ci) {
        if (DisarmManager.shouldDisarm()) ci.cancel();
    }
}