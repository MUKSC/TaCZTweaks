package me.muksc.tacztweaks.mixin.feature.disarm;

import com.tacz.guns.entity.shooter.LivingEntityCrawl;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityCrawl.class, remap = false)
public abstract class LivingEntityCrawlMixin {
    @Shadow @Final private LivingEntity shooter;

    @Inject(method = "crawl", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$crawl$disarm(boolean isCrawl, CallbackInfo ci) {
        if (DisarmManager.shouldDisarm(shooter)) ci.cancel();
    }

    @Inject(method = "tickCrawling", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$tickCrawling$disarm(CallbackInfo ci) {
        if (DisarmManager.shouldDisarm(shooter)) ci.cancel();
    }
}