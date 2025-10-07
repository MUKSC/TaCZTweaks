package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.client.event.CameraSetupEvent;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.gun.GunRecoil;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.mixininterface.modifiers.GunRecoilExtension;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CameraSetupEvent.class, remap = false)
public abstract class CameraSetupEventMixin {
    @Definition(id = "getCrawlRecoilMultiplier", method = "Lcom/tacz/guns/resource/pojo/data/gun/GunData;getCrawlRecoilMultiplier()F")
    @Expression("? * ?.getCrawlRecoilMultiplier()")
    @Inject(method = "initialCameraRecoil", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static void tacztweaks$initialCameraRecoil$setCrawl(GunFireEvent event, CallbackInfo ci, @Share("crawl") LocalBooleanRef crawlRef) {
        crawlRef.set(true);
    }

    @ModifyExpressionValue(method = "initialCameraRecoil", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/client/gameplay/IClientPlayerGunOperator;getClientAimingProgress(F)F"))
    private static float tacztweaks$initialCameraRecoil$storeAimingProgress(float original, @Share("aimingProgress") LocalFloatRef aimingProgressRef) {
        aimingProgressRef.set(original);
        return original;
    }

    @WrapOperation(method = "initialCameraRecoil", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunRecoil;genPitchSplineFunction(F)Lorg/apache/commons/math3/analysis/polynomials/PolynomialSplineFunction;"))
    private static PolynomialSplineFunction tacztweaks$initialCameraRecoil$verticalRecoilModifier(
        GunRecoil instance,
        float modifier,
        Operation<PolynomialSplineFunction> original,
        @Share("crawl") LocalBooleanRef crawlRef,
        @Share("aimingProgress") LocalFloatRef aimingProgressRef
    ) {
        GunRecoilExtension ext = (GunRecoilExtension) instance;
        try {
            ext.tacztweaks$setModifier(value -> {
                boolean negative = value < 0;
                double recoil = AttachmentPropertyManager.eval(Config.Modifiers.VerticalRecoil.INSTANCE.toTaCZ(), Math.abs(value));
                if (crawlRef.get()) recoil = AttachmentPropertyManager.eval(Config.Modifiers.CrawlVerticalRecoil.INSTANCE.toTaCZ(), recoil);
                double aimingModifier = AttachmentPropertyManager.eval(Config.Modifiers.AimVerticalRecoil.INSTANCE.toTaCZ(), recoil) - recoil;
                recoil = recoil + (aimingModifier * aimingProgressRef.get());
                return recoil * (negative ? -1 : 1);
            });
            return original.call(instance, modifier);
        } finally {
            ext.tacztweaks$setModifier(null);
        }
    }

    @WrapOperation(method = "initialCameraRecoil", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunRecoil;genYawSplineFunction(F)Lorg/apache/commons/math3/analysis/polynomials/PolynomialSplineFunction;"))
    private static PolynomialSplineFunction tacztweaks$initialCameraRecoil$horizontalRecoilModifier(
        GunRecoil instance,
        float modifier,
        Operation<PolynomialSplineFunction> original,
        @Share("crawl") LocalBooleanRef crawlRef,
        @Share("aimingProgress") LocalFloatRef aimingProgressRef
    ) {
        GunRecoilExtension ext = (GunRecoilExtension) instance;
        try {
            ext.tacztweaks$setModifier(value -> {
                boolean negative = value < 0;
                double recoil = AttachmentPropertyManager.eval(Config.Modifiers.HorizontalRecoil.INSTANCE.toTaCZ(), Math.abs(value));
                if (crawlRef.get()) recoil = AttachmentPropertyManager.eval(Config.Modifiers.CrawlHorizontalRecoil.INSTANCE.toTaCZ(), recoil);
                double aimingModifier = AttachmentPropertyManager.eval(Config.Modifiers.AimHorizontalRecoil.INSTANCE.toTaCZ(), recoil) - recoil;
                recoil = recoil + (aimingModifier * aimingProgressRef.get());
                return recoil * (negative ? -1 : 1);
            });
            return original.call(instance, modifier);
        } finally {
            ext.tacztweaks$setModifier(null);
        }
    }
}
