package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.visuals.tilt_on_crawl;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.client.animation.statemachine.GunAnimationStateContext;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.config.Config.AudioAndVisuals.Visuals.ETiltOnCrawl;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GunAnimationStateContext.class, remap = false)
public abstract class GunAnimationStateContextMixin {
    @WrapOperation(method = "lambda$shouldSlide$18", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isCrouching()Z", remap = true))
    private boolean tacztweaks$shouldSlide$tiltOnCrawl(Entity instance, Operation<Boolean> original) {
        ETiltOnCrawl tiltOnCrawl = Config.AudioAndVisuals.Visuals.tiltOnCrawl();
        if (tiltOnCrawl == ETiltOnCrawl.NEVER && instance.isVisuallyCrawling()) return false;
        return original.call(instance) || (tiltOnCrawl == ETiltOnCrawl.ALWAYS && instance.isVisuallyCrawling());
    }
}