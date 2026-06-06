package me.muksc.tacztweaks.mixin.feature.balancing.recoil;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.client.event.CameraSetupEvent;
import me.muksc.tacztweaks.TaCZTweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CameraSetupEvent.class, remap = false)
public abstract class CameraSetupEventMixin {
    @Definition(id = "getCrawlRecoilMultiplier", method = "Lcom/tacz/guns/resource/pojo/data/gun/GunData;getCrawlRecoilMultiplier()F")
    @Expression("? * ?.getCrawlRecoilMultiplier()")
    @Inject(method = "initialCameraRecoil", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static void tacztweaks$initialCameraRecoil$setCrawl(
        GunFireEvent event, CallbackInfo ci,
        @Share(value = "crawl", namespace = TaCZTweaks.MOD_ID) LocalBooleanRef crawlRef
    ) {
        crawlRef.set(true);
    }

    @ModifyExpressionValue(method = "initialCameraRecoil", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/client/gameplay/IClientPlayerGunOperator;getClientAimingProgress(F)F"))
    private static float tacztweaks$initialCameraRecoil$storeAimingProgress(
        float original,
        @Share(value = "aimingProgress", namespace = TaCZTweaks.MOD_ID) LocalFloatRef aimingProgressRef
    ) {
        aimingProgressRef.set(original);
        return original;
    }
}