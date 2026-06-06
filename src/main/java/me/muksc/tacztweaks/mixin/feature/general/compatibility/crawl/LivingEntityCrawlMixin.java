package me.muksc.tacztweaks.mixin.feature.general.compatibility.crawl;

import com.tacz.guns.entity.shooter.LivingEntityCrawl;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityCrawl.class, remap = false)
public abstract class LivingEntityCrawlMixin {
    @Inject(method = "crawl", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$crawl$forceDisableCrawl(boolean isCrawl, CallbackInfo ci) {
        if (Config.General.Compatibility.forceDisableCrawl()) ci.cancel();
    }

    @Inject(method = "tickCrawling", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$tickCrawling$forceDisableCrawl(CallbackInfo ci) {
        if (Config.General.Compatibility.forceDisableCrawl()) ci.cancel();
    }
}