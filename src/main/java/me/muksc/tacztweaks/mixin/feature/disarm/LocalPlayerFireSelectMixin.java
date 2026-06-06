package me.muksc.tacztweaks.mixin.feature.disarm;

import com.tacz.guns.client.gameplay.LocalPlayerFireSelect;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerFireSelect.class, remap = false)
public abstract class LocalPlayerFireSelectMixin {
    @Inject(method = "fireSelect", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$fireSelect$disarm(CallbackInfo ci) {
        if (DisarmManager.shouldDisarm()) ci.cancel();
    }
}