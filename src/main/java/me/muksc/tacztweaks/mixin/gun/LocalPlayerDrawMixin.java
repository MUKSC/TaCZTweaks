package me.muksc.tacztweaks.mixin.gun;

import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.client.gameplay.LocalPlayerDraw;
import me.muksc.tacztweaks.mixininterface.gun.LocalPlayerDataHolderExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerDraw.class, remap = false)
public abstract class LocalPlayerDrawMixin {
    @Shadow @Final private LocalPlayerDataHolder data;

    @Inject(method = "resetData", at = @At("TAIL"))
    private void tacztweaks$resetData(CallbackInfo ci) {
        LocalPlayerDataHolderExtension ext = (LocalPlayerDataHolderExtension) data;
        ext.tacztweaks$setShouldStartReloading(false);
    }
}
