package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.visuals.smoother_crawl_animation;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    //~ if >=1.20.5 'setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V' -> 'setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V'
    @ModifyExpressionValue(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getSwimAmount(F)F"))
    private float tacztweaks$setupRotations$visualTweak$storeSwimAmount(
        float original,
        @Share("swimAmount") LocalFloatRef swimAmountRef
    ) {
        swimAmountRef.set(original);
        return original;
    }

    //~ if >=1.20.5 'setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V' -> 'setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V'
    @ModifyExpressionValue(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isVisuallySwimming()Z"))
    private boolean tacztweaks$setupRotations$visualTweak$alwaysTranslate(boolean original) {
        return original || Config.AudioAndVisuals.Visuals.smootherCrawlAnimation();
    }

    //~ if >=1.20.5 'setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V' -> 'setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V'
    @ModifyArg(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"), index = 1)
    private float tacztweaks$setupRotations$visualTweak$modifyYTranslate(
        float pY,
        @Share("swimAmount") LocalFloatRef swimAmountRef
    ) {
        return Config.AudioAndVisuals.Visuals.smootherCrawlAnimation()
            ? Mth.lerp(swimAmountRef.get(), 0.0F, pY - 0.4F)
            : pY;
    }

    //~ if >=1.20.5 'setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V' -> 'setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V'
    @ModifyArg(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"), index = 2)
    private float tacztweaks$setupRotations$visualTweak$lerpZTranslate(
        float pZ,
        @Share("swimAmount") LocalFloatRef swimAmountRef
    ) {
        return Config.AudioAndVisuals.Visuals.smootherCrawlAnimation()
            ? Mth.lerp(swimAmountRef.get(), 0.0F, pZ)
            : pZ;
    }
}