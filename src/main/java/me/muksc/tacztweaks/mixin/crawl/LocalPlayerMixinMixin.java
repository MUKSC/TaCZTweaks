package me.muksc.tacztweaks.mixin.crawl;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.tacz.guns.client.gameplay.LocalPlayerCrawl;
import me.muksc.tacztweaks.Config;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayer.class, priority = 1500, remap = false)
public abstract class LocalPlayerMixinMixin {
    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.LocalPlayerMixin",
        name = "crawl"
    )
    @WrapWithCondition(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerCrawl;crawl(Z)V"))
    private boolean tacztweaks$crawl$disableCrawl(LocalPlayerCrawl instance, boolean isCrawl) {
        return Config.Crawl.INSTANCE.enabled();
    }

    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.LocalPlayerMixin",
        name = "onTickClientSide"
    )
    @WrapWithCondition(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerCrawl;tickCrawl()V"))
    private boolean tacztweaks$onTickClientSide$disableTickCrawl(LocalPlayerCrawl instance) {
        return Config.Crawl.INSTANCE.enabled();
    }
}
