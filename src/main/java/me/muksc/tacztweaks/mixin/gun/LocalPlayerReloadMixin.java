package me.muksc.tacztweaks.mixin.gun;

import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.client.gameplay.LocalPlayerReload;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.mixininterface.gun.LocalPlayerDataHolderExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerReload.class, remap = false)
public abstract class LocalPlayerReloadMixin {
    @Shadow @Final private LocalPlayerDataHolder data;

    @Inject(method = "reload", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$reload$boltBeforeReload(CallbackInfo ci) {
        if (!Config.Gun.INSTANCE.manualBolting()) return;
        LocalPlayerDataHolderExtension ext = (LocalPlayerDataHolderExtension) data;
        if (ext.tacztweaks$getShouldStartReloading()) return;

        ext.tacztweaks$setShouldStartReloading(true);
        ci.cancel();
    }
}
