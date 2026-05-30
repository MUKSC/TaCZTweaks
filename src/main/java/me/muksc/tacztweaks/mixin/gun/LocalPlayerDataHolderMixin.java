package me.muksc.tacztweaks.mixin.gun;

import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import me.muksc.tacztweaks.mixininterface.gun.LocalPlayerDataHolderExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerDataHolder.class, remap = false)
public abstract class LocalPlayerDataHolderMixin implements LocalPlayerDataHolderExtension {
    @Unique
    boolean tacztweaks$boltBeforeReload = false;

    @Override
    public boolean tacztweaks$getBoltBeforeReload() {
        return tacztweaks$boltBeforeReload;
    }

    @Override
    public void tacztweaks$setBoltBeforeReload(boolean boltBeforeReload) {
        tacztweaks$boltBeforeReload = boltBeforeReload;
    }

    @Inject(method = "reset", at = @At("TAIL"))
    private void tacztweaks$reset(CallbackInfo ci) {
        tacztweaks$boltBeforeReload = false;
    }
}