package me.muksc.tacztweaks.mixin.tweaks;

import com.mojang.blaze3d.platform.Window;
import com.tacz.guns.client.event.RenderCrosshairEvent;
import me.muksc.tacztweaks.Config;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderCrosshairEvent.class, remap = false)
public abstract class RenderCrosshairEventMixin {
    @Inject(method = "renderHitMarker", at = @At("HEAD"), cancellable = true)
    private static void taczweaks$renderHitMarker$conditional(GuiGraphics graphics, Window window, CallbackInfo ci) {
        if (!Config.Tweaks.INSTANCE.hideHitMarkers()) return;
        ci.cancel();
    }
}
