package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.modifier.custom.InaccuracyModifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunFireModeAdjustData;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import me.muksc.tacztweaks.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InaccuracyModifier.class, remap = false)
public abstract class InaccuracyModifierMixin {
    @ModifyExpressionValue(method = "lambda$initCache$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunData;getInaccuracy(Lcom/tacz/guns/resource/pojo/data/gun/InaccuracyType;F)F"))
    private static float tacztweaks$initCache$inaccuracyModifier(float original, @Local(argsOnly = true) InaccuracyType type) {
        float inaccuracy = (float) AttachmentPropertyManager.eval(Config.Modifiers.INSTANCE.inaccuracy(), original);
        if (type == InaccuracyType.AIM) inaccuracy = (float) AttachmentPropertyManager.eval(Config.Modifiers.INSTANCE.aimInaccuracy(), inaccuracy);
        return inaccuracy;
    }

    @Inject(method = "buildNormal", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;"))
    private void tacztweaks$buildNormal$inaccuracyModifier(
        GunData gunData,
        AttachmentCacheProperty cacheProperty,
        GunFireModeAdjustData fireModeAdjustData,
        InaccuracyType type,
        String titleKey,
        double referenceValue,
        CallbackInfoReturnable<IAttachmentModifier.DiagramsData> cir,
        @Local(ordinal = 0) LocalFloatRef inaccuracyRef
    ) {
        inaccuracyRef.set((float) AttachmentPropertyManager.eval(Config.Modifiers.INSTANCE.inaccuracy(), inaccuracyRef.get()));
    }

    @Inject(method = "buildAim", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;"))
    private void tacztweaks$buildAim$inaccuracyModifier(
        GunData gunData,
        AttachmentCacheProperty cacheProperty,
        GunFireModeAdjustData fireModeAdjustData,
        CallbackInfoReturnable<IAttachmentModifier.DiagramsData> cir,
        @Local(ordinal = 0) LocalFloatRef inaccuracyRef
    ) {
        float inaccuracy = (float) AttachmentPropertyManager.eval(Config.Modifiers.INSTANCE.inaccuracy(), 1.0F - inaccuracyRef.get());
        inaccuracy = (float) AttachmentPropertyManager.eval(Config.Modifiers.INSTANCE.aimInaccuracy(), inaccuracy);
        inaccuracyRef.set(1.0F - inaccuracy);
    }
}
