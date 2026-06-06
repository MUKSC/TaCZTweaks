package me.muksc.tacztweaks.mixin.feature.general.fixes.crawl_cooldown_fix;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.client.gameplay.LocalPlayerCrawl;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerCrawl.class, remap = false)
public abstract class LocalPlayerCrawlMixin {
    @Shadow private int crawCooldownTicks;

    @Definition(id = "crawCooldownTicks", field = "Lcom/tacz/guns/client/gameplay/LocalPlayerCrawl;crawCooldownTicks:I")
    @Expression("this.crawCooldownTicks > 0")
    @ModifyExpressionValue(method = "crawl", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$crawl$crawlCooldownFix(
        boolean original,
        @Local(argsOnly = true) boolean isCrawl
    ) {
        if (!Config.General.Fixes.crawlCooldownFix()) return original;
        return original && isCrawl;
    }

    @Definition(id = "crawCooldownTicks", field = "Lcom/tacz/guns/client/gameplay/LocalPlayerCrawl;crawCooldownTicks:I")
    @Expression("this.crawCooldownTicks = 10")
    @WrapWithCondition(method = "lambda$crawl$0", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$crawl$crawlCooldownFix(
        LocalPlayerCrawl instance, int value,
        @Local(argsOnly = true) boolean isCrawl
    ) {
        if (!Config.General.Fixes.crawlCooldownFix()) return true;
        return isCrawl && crawCooldownTicks <= 0;
    }
}