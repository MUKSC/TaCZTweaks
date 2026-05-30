package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.custom.InaccuracyModifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunFireModeAdjustData;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import me.muksc.tacztweaks.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InaccuracyModifier.class, remap = false)
public abstract class InaccuracyModifierMixin {
    @ModifyExpressionValue(method = "lambda$initCache$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunData;getInaccuracy(Lcom/tacz/guns/resource/pojo/data/gun/InaccuracyType;F)F"))
    private static float tacztweaks$initCache$inaccuracyModifier(float original, @Local(argsOnly = true) InaccuracyType type) {
        float inaccuracy = (float) Config.Modifiers.Inaccuracy.INSTANCE.eval(original);
        switch (type) {
            case STAND -> inaccuracy = (float) Config.Modifiers.StandInaccuracy.INSTANCE.eval(inaccuracy);
            case AIM -> inaccuracy = (float) Config.Modifiers.AimInaccuracy.INSTANCE.eval(inaccuracy);
            case MOVE -> inaccuracy = (float) Config.Modifiers.MoveInaccuracy.INSTANCE.eval(inaccuracy);
            case SNEAK -> inaccuracy = (float) Config.Modifiers.SneakInaccuracy.INSTANCE.eval(inaccuracy);
            case LIE -> inaccuracy = (float) Config.Modifiers.CrawlInaccuracy.INSTANCE.eval(inaccuracy);
        }
        return inaccuracy;
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "buildNormal", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;"))
    private void tacztweaks$buildNormal$inaccuracyModifier(
        GunData gunData,
        AttachmentCacheProperty cacheProperty,
        GunFireModeAdjustData fireModeAdjustData,
        InaccuracyType type,
        String titleKey,
        double referenceValue,
        CallbackInfoReturnable<Object> cir,
        @Local(ordinal = 0) LocalFloatRef inaccuracyRef
    ) {
        inaccuracyRef.set((float) Config.Modifiers.Inaccuracy.INSTANCE.eval(inaccuracyRef.get()));
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "buildAim", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;"))
    private void tacztweaks$buildAim$inaccuracyModifier(
        GunData gunData,
        AttachmentCacheProperty cacheProperty,
        GunFireModeAdjustData fireModeAdjustData,
        CallbackInfoReturnable<Object> cir,
        @Local(ordinal = 0) LocalFloatRef inaccuracyRef
    ) {
        float inaccuracy = (float) Config.Modifiers.Inaccuracy.INSTANCE.eval(1.0F - inaccuracyRef.get());
        inaccuracy = (float) Config.Modifiers.AimInaccuracy.INSTANCE.eval(inaccuracy);
        inaccuracyRef.set(1.0F - inaccuracy);
    }
}