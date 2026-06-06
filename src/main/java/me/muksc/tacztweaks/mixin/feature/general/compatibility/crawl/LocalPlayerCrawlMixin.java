package me.muksc.tacztweaks.mixin.feature.general.compatibility.crawl;

import com.tacz.guns.client.gameplay.LocalPlayerCrawl;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerCrawl.class, remap = false)
public abstract class LocalPlayerCrawlMixin {
    @Inject(method = "crawl", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$crawl$forceDisableCrawl(boolean isCrawl, CallbackInfo ci) {
        if (Config.General.Compatibility.forceDisableCrawl()) ci.cancel();
    }

    @Inject(method = "tickCrawl", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$tickCrawl$forceDisableCrawl(CallbackInfo ci) {
        if (Config.General.Compatibility.forceDisableCrawl()) ci.cancel();
    }
}