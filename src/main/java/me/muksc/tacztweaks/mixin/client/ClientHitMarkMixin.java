package me.muksc.tacztweaks.mixin.client;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.client.event.ClientHitMark;
import me.muksc.tacztweaks.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientHitMark.class, remap = false)
public abstract class ClientHitMarkMixin {
    @Inject(method = "onEntityHurt", at = @At("HEAD"), cancellable = true)
    private static void tacztweaks$onEntityHurt$conditional(EntityHurtByGunEvent.Post event, CallbackInfo ci) {
        if (!Config.Tweaks.INSTANCE.hideHitMarkers()) return;
        ci.cancel();
    }

    @Inject(method = "onEntityKill", at = @At("HEAD"), cancellable = true)
    private static void tacztweaks$onEntityKill$conditional(EntityKillByGunEvent event, CallbackInfo ci) {
        if (!Config.Tweaks.INSTANCE.hideHitMarkers()) return;
        ci.cancel();
    }
}
