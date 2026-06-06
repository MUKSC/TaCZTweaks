package me.muksc.tacztweaks.mixin.feature.balancing.recoil;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.resource.pojo.data.gun.GunRecoil;
import me.muksc.tacztweaks.mixininterface.feature.balancing.recoil.DynamicGunRecoil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(value = GunRecoil.class, remap = false)
public abstract class GunRecoilMixin implements DynamicGunRecoil {
    @Unique
    private Function<Double, Double> tacztweaks$dynamicModifierMapper = null;

    @Override
    public void tacztweaks$setDynamicModifierMapper(Function<Double, Double> mapper) {
        tacztweaks$dynamicModifierMapper = mapper;
    }

    @Definition(id = "modifier", local = @Local(type = float.class, argsOnly = true))
    @Expression("? * (double) modifier")
    @ModifyExpressionValue(method = "getSplineFunction", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double tacztweaks$getSplineFunction$modifier(double original) {
        if (tacztweaks$dynamicModifierMapper == null) return original;
        return tacztweaks$dynamicModifierMapper.apply(original);
    }
}