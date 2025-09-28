package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.resource.pojo.data.gun.GunRecoil;
import me.muksc.tacztweaks.mixininterface.modifiers.GunRecoilExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(value = GunRecoil.class, remap = false)
public abstract class GunRecoilMixin implements GunRecoilExtension {
    @Unique
    private Function<Double, Double> tacztweaks$modifier = null;

    @Override
    public void tacztweaks$setModifier(Function<Double, Double> modifier) {
        tacztweaks$modifier = modifier;
    }

    @Definition(id = "modifier", local = @Local(type = float.class, argsOnly = true))
    @Expression("? * (double) modifier")
    @ModifyExpressionValue(method = "getSplineFunction", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double tacztweaks$getSplineFunction$modifier(double original) {
        if (tacztweaks$modifier == null) return original;
        return tacztweaks$modifier.apply(original);
    }
}
