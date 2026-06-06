package me.muksc.tacztweaks.mixin.feature.attribute.capability.reloading;

import com.tacz.guns.client.gameplay.LocalPlayerReload;
import me.muksc.tacztweaks.core.extension.DeferredHolderExt;
import me.muksc.tacztweaks.registry.ModAttributes;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerReload.class, remap = false)
public abstract class LocalPlayerReloadMixin {
    @Shadow @Final private LocalPlayer player;

    @Inject(method = "reload", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$reload$attribute$capability$reloading(CallbackInfo ci) {
        double value = player.getAttributeValue(DeferredHolderExt.valueOrDelegate(ModAttributes.RELOADING));
        if (value <= 0.0) ci.cancel();
    }
}