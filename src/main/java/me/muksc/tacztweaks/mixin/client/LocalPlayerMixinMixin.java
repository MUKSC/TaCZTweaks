package me.muksc.tacztweaks.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.client.gameplay.LocalPlayerCrawl;
import com.tacz.guns.client.gameplay.LocalPlayerReload;
import me.muksc.tacztweaks.Config;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayer.class, priority = 1500, remap = false)
public abstract class LocalPlayerMixinMixin {
    @Dynamic
    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.LocalPlayerMixin",
        name = "swapSprintStatus"
    )
    @WrapWithCondition(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerReload;cancelReload()V"))
    private boolean sprintWhileReloading(LocalPlayerReload instance, @Local(argsOnly = true) boolean sprinting) {
        return sprinting && !Config.sprintWhileReloading;
    }

    @Dynamic
    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.LocalPlayerMixin",
        name = "crawl"
    )
    @WrapWithCondition(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerCrawl;crawl(Z)V"))
    private boolean disableCrawl(LocalPlayerCrawl instance, boolean isCrawl) {
        return !Config.disableTaCZCrawl;
    }

    @Dynamic
    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.LocalPlayerMixin",
        name = "onTickClientSide"
    )
    @WrapWithCondition(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerCrawl;tickCrawl()V"))
    private boolean disableTickCrawl(LocalPlayerCrawl instance) {
        return !Config.disableTaCZCrawl;
    }
}
