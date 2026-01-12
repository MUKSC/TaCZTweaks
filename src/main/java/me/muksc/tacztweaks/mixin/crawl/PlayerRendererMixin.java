package me.muksc.tacztweaks.mixin.crawl;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    @ModifyExpressionValue(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isVisuallySwimming()Z"))
    private boolean tacztweaks$setupRotations$translateAlways(boolean original) {
        if (!Config.Crawl.INSTANCE.visualTweak()) return original;
        return true;
    }

    @ModifyArg(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"), index = 1)
    private float tacztweaks$setupRotations$modifyYTranslate(float pY, @Local(ordinal = 3) float f) {
        if (!Config.Crawl.INSTANCE.visualTweak()) return pY;
        return Mth.lerp(f, 0.0F, pY - 0.4F);
    }

    @ModifyArg(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"), index = 2)
    private float tacztweaks$setupRotations$lerpZTranslate(float pZ, @Local(ordinal = 3) float f) {
        if (!Config.Crawl.INSTANCE.visualTweak()) return pZ;
        return Mth.lerp(f, 0.0F, pZ);
    }
}
