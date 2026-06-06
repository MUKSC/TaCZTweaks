package me.muksc.tacztweaks.mixin.feature.disarm;

import com.tacz.guns.client.gameplay.LocalPlayerCrawl;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerCrawl.class, remap = false)
public abstract class LocalPlayerCrawlMixin {
    @Inject(method = "crawl", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$crawl$disarm(boolean isCrawl, CallbackInfo ci) {
        if (DisarmManager.shouldDisarm()) ci.cancel();
    }

    @Inject(method = "tickCrawl", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$tickCrawl$disarm(CallbackInfo ci) {
        if (DisarmManager.shouldDisarm()) ci.cancel();
    }
}