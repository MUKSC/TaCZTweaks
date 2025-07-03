package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.modifier.custom.RecoilModifier;
import me.muksc.tacztweaks.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RecoilModifier.class, remap = false)
public abstract class RecoilModifierMixin {
    @ModifyExpressionValue(method = "initCache", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/custom/RecoilModifier;getMaxInGunRecoilKeyFrame([Lcom/tacz/guns/resource/pojo/data/gun/GunRecoilKeyFrame;)F", ordinal = 0))
    private float tacztweaks$initCache$verticalRecoilModifier(float original) {
        return (float) AttachmentPropertyManager.eval(Config.Modifiers.INSTANCE.verticalRecoil(), original);
    }

    @OnlyIn(Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/custom/RecoilModifier;getMaxInGunRecoilKeyFrame([Lcom/tacz/guns/resource/pojo/data/gun/GunRecoilKeyFrame;)F", ordinal = 0))
    private float tacztweaks$getPropertyDiagramsData$verticalRecoilModifier(float original) {
        return (float) AttachmentPropertyManager.eval(Config.Modifiers.INSTANCE.verticalRecoil(), original);
    }

    @ModifyExpressionValue(method = "initCache", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/custom/RecoilModifier;getMaxInGunRecoilKeyFrame([Lcom/tacz/guns/resource/pojo/data/gun/GunRecoilKeyFrame;)F", ordinal = 1))
    private float tacztweaks$initCache$horizontalRecoilModifier(float original) {
        return (float) AttachmentPropertyManager.eval(Config.Modifiers.INSTANCE.horizontalRecoil(), original);
    }

    @OnlyIn(Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/custom/RecoilModifier;getMaxInGunRecoilKeyFrame([Lcom/tacz/guns/resource/pojo/data/gun/GunRecoilKeyFrame;)F", ordinal = 1))
    private float tacztweaks$getPropertyDiagramsData$horizontalRecoilModifier(float original) {
        return (float) AttachmentPropertyManager.eval(Config.Modifiers.INSTANCE.horizontalRecoil(), original);
    }
}
