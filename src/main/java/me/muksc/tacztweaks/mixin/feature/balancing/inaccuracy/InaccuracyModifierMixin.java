package me.muksc.tacztweaks.mixin.feature.balancing.inaccuracy;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.custom.InaccuracyModifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunFireModeAdjustData;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InaccuracyModifier.class, remap = false)
public abstract class InaccuracyModifierMixin {
    @ModifyExpressionValue(method = "lambda$initCache$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunData;getInaccuracy(Lcom/tacz/guns/resource/pojo/data/gun/InaccuracyType;F)F"))
    private static float tacztweaks$initCache$inaccuracyModifier(
        float original,
        @Local(argsOnly = true) InaccuracyType type
    ) {
        float inaccuracy = (float) Config.Balancing.Inaccuracy.eval(original);
        switch (type) {
            case STAND -> inaccuracy = (float) Config.Balancing.StandInaccuracy.eval(inaccuracy);
            case AIM -> inaccuracy = (float) Config.Balancing.AimInaccuracy.eval(inaccuracy);
            case MOVE -> inaccuracy = (float) Config.Balancing.MoveInaccuracy.eval(inaccuracy);
            case SNEAK -> inaccuracy = (float) Config.Balancing.SneakInaccuracy.eval(inaccuracy);
            case LIE -> inaccuracy = (float) Config.Balancing.CrawlInaccuracy.eval(inaccuracy);
        }
        return inaccuracy;
    }

    //~ environment environment_client
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    @Inject(method = "buildNormal", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;"))
    private void tacztweaks$buildNormal$inaccuracyModifier(
        GunData gunData, AttachmentCacheProperty cacheProperty, GunFireModeAdjustData fireModeAdjustData, InaccuracyType type, String titleKey, double referenceValue, CallbackInfoReturnable<Object> cir,
        @Local(name = "inaccuracy") LocalFloatRef inaccuracyRef
    ) {
        inaccuracyRef.set((float) Config.Balancing.Inaccuracy.eval(inaccuracyRef.get()));
    }

    //~ environment environment_client
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    @Inject(method = "buildAim", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;"))
    private void tacztweaks$buildAim$inaccuracyModifier(
        GunData gunData, AttachmentCacheProperty cacheProperty, GunFireModeAdjustData fireModeAdjustData, CallbackInfoReturnable<Object> cir,
        @Local(name = "aimInaccuracy") LocalFloatRef aimInaccuracyRef
    ) {
        float inaccuracy = (float) Config.Balancing.Inaccuracy.eval(1.0F - aimInaccuracyRef.get());
        inaccuracy = (float) Config.Balancing.AimInaccuracy.eval(inaccuracy);
        aimInaccuracyRef.set(1.0F - inaccuracy);
    }
}