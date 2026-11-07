package me.muksc.tacztweaks.mixin.gun.unload;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.gameplay.LocalPlayerUnload;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerUnload.class, remap = false)
public abstract class LocalPlayerUnloadMixin {
    @ModifyExpressionValue(method = "unloadAmmo", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerUnload;isLocalIdle()Z"))
    private boolean tacztweaks$unload$allowUnload(boolean original) {
        return original || !Config.Gun.INSTANCE.allowUnload();
    }
}