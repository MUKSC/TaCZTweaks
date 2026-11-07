package me.muksc.tacztweaks.mixin.feature.disarm;

import com.tacz.guns.client.input.InteractKey;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InteractKey.class, remap = false)
public abstract class InteractKeyMixin {
    @Inject(method = "onInteractPress", at = @At("HEAD"), cancellable = true)
    private static void tacztweaks$onInteractPress$disarm(CallbackInfoReturnable<Boolean> cir) {
        if (DisarmManager.shouldDisarm()) cir.setReturnValue(false);
    }
}