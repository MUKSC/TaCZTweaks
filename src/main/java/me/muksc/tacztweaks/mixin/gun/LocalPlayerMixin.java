package me.muksc.tacztweaks.mixin.gun;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.animation.statemachine.GunAnimationStateContext;
import com.tacz.guns.client.resource.GunDisplayInstance;
import me.muksc.tacztweaks.client.input.TiltGunKey;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.mixininterface.gun.SlideDataHolder;
import me.muksc.tacztweaks.network.NetworkHandler;
import me.muksc.tacztweaks.network.message.ClientMessagePlayerShouldSlide;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements SlideDataHolder {
    @ModifyReturnValue(method = "canStartSprinting", at = @At("RETURN"))
    private boolean tacztweaks$canStartSprinting$tiltGunKeyCancelsSprint(boolean original) {
        if (!Config.Gun.INSTANCE.tiltGunKeyCancelsSprint()) return original;
        var instance = LocalPlayer.class.cast(this);
        return original && !TiltGunKey.isActive(instance);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tacztweaks$tick$tiltCheck(CallbackInfo ci) {
        Supplier<Boolean> supplier = () -> {
            LocalPlayer player = LocalPlayer.class.cast(this);
            if (!IGun.mainHandHoldGun(player)) return false;
            GunDisplayInstance display = TimelessAPI.getGunDisplay(player.getMainHandItem()).orElse(null);
            if (display == null) return false;
            GunAnimationStateContext context = display.getAnimationStateMachine().getContext();
            if (context == null) return false;
            return context.shouldSlide();
        };
        boolean shouldSlide = supplier.get();
        if (shouldSlide != tacztweaks$getShouldSlide()) NetworkHandler.INSTANCE.sendC2S(new ClientMessagePlayerShouldSlide(shouldSlide));
        tacztweaks$setShouldSlide(shouldSlide);
    }
}
