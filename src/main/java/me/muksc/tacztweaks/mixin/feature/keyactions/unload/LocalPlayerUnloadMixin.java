package me.muksc.tacztweaks.mixin.feature.keyactions.unload;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.gameplay.LocalPlayerUnload;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerUnload.class, remap = false)
public abstract class LocalPlayerUnloadMixin {
    @ModifyExpressionValue(method = "unloadAmmo", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerUnload;isLocalIdle()Z"))
    private boolean tacztweaks$unloadAmmo$unloadKey$allowUnload(boolean original) {
        return original || !Config.KeyActions.Unload.enabled();
    }
}