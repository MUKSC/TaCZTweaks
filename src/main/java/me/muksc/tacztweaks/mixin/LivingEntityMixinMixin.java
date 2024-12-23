package me.muksc.tacztweaks.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.tacz.guns.entity.shooter.LivingEntityCrawl;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.ShooterDataHolderProvider;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LivingEntity.class, priority = 1500, remap = false)
public abstract class LivingEntityMixinMixin implements ShooterDataHolderProvider {
    @SuppressWarnings("MissingUnique")
    @Final private ShooterDataHolder tacz$data;

    @Override
    public ShooterDataHolder tacztweaks$getShooterDataHolder() {
        return tacz$data;
    }

    @TargetHandler(
        mixin = "com.tacz.guns.mixin.common.LivingEntityMixin",
        name = "crawl"
    )
    @WrapWithCondition(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/entity/shooter/LivingEntityCrawl;crawl(Z)V"))
    private boolean disableCrawl(LivingEntityCrawl instance, boolean isCrawl) {
        return !Config.disableTaCZCrawl;
    }

    @TargetHandler(
        mixin = "com.tacz.guns.mixin.common.LivingEntityMixin",
        name = "onTickServerSide"
    )
    @WrapWithCondition(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/entity/shooter/LivingEntityCrawl;tickCrawling()V"))
    private boolean disableTickCrawling(LivingEntityCrawl instance) {
        return !Config.disableTaCZCrawl;
    }
}
