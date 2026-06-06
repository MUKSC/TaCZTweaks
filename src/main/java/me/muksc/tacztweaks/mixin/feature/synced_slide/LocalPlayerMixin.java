package me.muksc.tacztweaks.mixin.feature.synced_slide;

import me.muksc.tacztweaks.feature.synced_slide.SyncedSlide;
import me.muksc.tacztweaks.mixininterface.feature.synced_slide.SlideDataHolder;
import me.muksc.tacztweaks.network.NetworkManager;
import me.muksc.tacztweaks.network.message.ClientMessagePlayerShouldSlide;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements SlideDataHolder {
    @Inject(method = "tick", at = @At("TAIL"))
    private void tacztweaks$tick$updateShouldSlide(CallbackInfo ci) {
        boolean shouldSlide = SyncedSlide.getShouldSlide(LocalPlayer.class.cast(this));
        if (shouldSlide != tacztweaks$getShouldSlide()) NetworkManager.sendC2S(new ClientMessagePlayerShouldSlide(shouldSlide));
        tacztweaks$setShouldSlide(shouldSlide);
    }
}