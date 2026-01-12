package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
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
        float inaccuracy = (float) AttachmentPropertyManager.eval(Config.Modifiers.Inaccuracy.INSTANCE.toTaCZ(), original);
        switch (type) {
            case STAND -> inaccuracy = (float) AttachmentPropertyManager.eval(Config.Modifiers.StandInaccuracy.INSTANCE.toTaCZ(), inaccuracy);
            case AIM -> inaccuracy = (float) AttachmentPropertyManager.eval(Config.Modifiers.AimInaccuracy.INSTANCE.toTaCZ(), inaccuracy);
            case MOVE -> inaccuracy = (float) AttachmentPropertyManager.eval(Config.Modifiers.MoveInaccuracy.INSTANCE.toTaCZ(), inaccuracy);
            case SNEAK -> inaccuracy = (float) AttachmentPropertyManager.eval(Config.Modifiers.SneakInaccuracy.INSTANCE.toTaCZ(), inaccuracy);
            case LIE -> inaccuracy = (float) AttachmentPropertyManager.eval(Config.Modifiers.CrawlInaccuracy.INSTANCE.toTaCZ(), inaccuracy);
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
        inaccuracyRef.set((float) AttachmentPropertyManager.eval(Config.Modifiers.Inaccuracy.INSTANCE.toTaCZ(), inaccuracyRef.get()));
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
        float inaccuracy = (float) AttachmentPropertyManager.eval(Config.Modifiers.Inaccuracy.INSTANCE.toTaCZ(), 1.0F - inaccuracyRef.get());
        inaccuracy = (float) AttachmentPropertyManager.eval(Config.Modifiers.AimInaccuracy.INSTANCE.toTaCZ(), inaccuracy);
        inaccuracyRef.set(1.0F - inaccuracy);
    }
}
