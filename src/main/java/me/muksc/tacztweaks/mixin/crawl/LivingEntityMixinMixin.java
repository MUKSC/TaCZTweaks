package me.muksc.tacztweaks.mixin.crawl;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.tacz.guns.entity.shooter.LivingEntityCrawl;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LivingEntity.class, priority = 1500, remap = false)
public abstract class LivingEntityMixinMixin {
    @Dynamic
    @TargetHandler(
        mixin = "com.tacz.guns.mixin.common.LivingEntityMixin",
        name = "crawl"
    )
    @WrapWithCondition(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/entity/shooter/LivingEntityCrawl;crawl(Z)V"))
    private boolean tacztweaks$crawl$disableCrawl(LivingEntityCrawl instance, boolean isCrawl) {
        return Config.Crawl.INSTANCE.enabled();
    }

    @Dynamic
    @TargetHandler(
        mixin = "com.tacz.guns.mixin.common.LivingEntityMixin",
        name = "onTickServerSide"
    )
    @WrapWithCondition(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/entity/shooter/LivingEntityCrawl;tickCrawling()V"))
    private boolean tacztweaks$onTickServerSide$disableTickCrawling(LivingEntityCrawl instance) {
        return Config.Crawl.INSTANCE.enabled();
    }
}
