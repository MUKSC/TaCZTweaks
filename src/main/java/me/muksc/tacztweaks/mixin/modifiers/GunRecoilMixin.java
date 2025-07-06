package me.muksc.tacztweaks.mixin.modifiers;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.gun.GunRecoil;
import me.muksc.tacztweaks.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = GunRecoil.class, remap = false)
public abstract class GunRecoilMixin {
    @ModifyArg(method = "genPitchSplineFunction", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunRecoil;getSplineFunction([Lcom/tacz/guns/resource/pojo/data/gun/GunRecoilKeyFrame;F)Lorg/apache/commons/math3/analysis/polynomials/PolynomialSplineFunction;"), index = 1)
    private float tacztweaks$genPitchSplineFunction$verticalRecoilModifier(float modifier) {
        return (float) AttachmentPropertyManager.eval(Config.Modifiers.VerticalRecoil.INSTANCE.toTaCZ(), modifier);
    }

    @ModifyArg(method = "genYawSplineFunction", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunRecoil;getSplineFunction([Lcom/tacz/guns/resource/pojo/data/gun/GunRecoilKeyFrame;F)Lorg/apache/commons/math3/analysis/polynomials/PolynomialSplineFunction;"), index = 1)
    private float tacztweaks$genYawSplineFunction$horizontalRecoilModifier(float modifier) {
        return (float) AttachmentPropertyManager.eval(Config.Modifiers.HorizontalRecoil.INSTANCE.toTaCZ(), modifier);
    }
}
