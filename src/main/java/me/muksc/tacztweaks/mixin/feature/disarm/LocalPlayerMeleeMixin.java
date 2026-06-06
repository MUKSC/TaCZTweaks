package me.muksc.tacztweaks.mixin.feature.disarm;

import com.tacz.guns.client.gameplay.LocalPlayerMelee;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerMelee.class, remap = false)
public abstract class LocalPlayerMeleeMixin {
    @Inject(method = "melee", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$melee$disarm(CallbackInfo ci) {
        if (DisarmManager.shouldDisarm()) ci.cancel();
    }
}