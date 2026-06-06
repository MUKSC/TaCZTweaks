package me.muksc.tacztweaks.mixin.feature.disarm;

import com.tacz.guns.client.input.InteractKey;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if forge {
import net.minecraftforge.client.event.InputEvent;
//?} else if neoforge {
/*import net.neoforged.neoforge.client.event.InputEvent;
*///?} else if fabric {
/*import cn.sh1rocu.tacz.api.event.InputEvent;
*///?}

@Mixin(value = InteractKey.class, remap = false)
public abstract class InteractKeyMixin {
    @Inject(method = "onInteractKeyPress", at = @At("HEAD"), cancellable = true)
    private static void tacztweaks$onInteractKeyPress$disarm(InputEvent.Key event, CallbackInfo ci) {
        if (DisarmManager.shouldDisarm()) ci.cancel();
    }

    @Inject(method = "onInteractMousePress", at = @At("HEAD"), cancellable = true)
    private static void tacztweaks$onInteractMousePress$disarm(InputEvent.MouseButton.Post event, CallbackInfo ci) {
        if (DisarmManager.shouldDisarm()) ci.cancel();
    }

    @Inject(method = "onInteractControllerPress", at = @At("HEAD"), cancellable = true)
    private static void tacztweaks$onInteractControllerPress$disarm(boolean isPress, CallbackInfoReturnable<Boolean> cir) {
        if (DisarmManager.shouldDisarm()) cir.setReturnValue(false);
    }
}