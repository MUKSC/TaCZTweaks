package me.muksc.tacztweaks.mixin.feature.datapack.shield;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.muksc.tacztweaks.feature.datapack.shield.CustomShieldResult;
import me.muksc.tacztweaks.mixininterface.feature.datapack.shield.CustomShieldEntity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

//? if fabric {
/*import com.llamalad7.mixinextras.sugar.Local;
*///?}
//? if fabric || forge
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements CustomShieldEntity {
    @Unique
    private CustomShieldResult tacztweaks$customShieldResult = null;

    @Override
    public CustomShieldResult tacztweaks$getShieldResult() {
        return tacztweaks$customShieldResult;
    }

    @Override
    public void tacztweaks$setShieldResult(CustomShieldResult result) {
        tacztweaks$customShieldResult = result;
    }

    //? if fabric {
    /*@Definition(id = "amount", local = @Local(type = float.class, argsOnly = true, ordinal = 0))
    @Expression("amount = @(0.0)")
    @ModifyExpressionValue(method = "hurt", at = @At("MIXINEXTRAS:EXPRESSION"))
    private float tacztweaks$hurt$shield$damage(float original) {
        if (!(tacztweaks$customShieldResult instanceof CustomShieldResult.Blocked blocked)) return original;
        return blocked.damage();
    }
    *///?}

    //? if fabric || forge {
    @ModifyArg(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"), index = 0)
    private float tacztweaks$hurt$shield$durabilityDamage(float damageAmount) {
        if (!(tacztweaks$customShieldResult instanceof CustomShieldResult.Blocked blocked)) return damageAmount;
        return blocked.durabilityDamage().apply(damageAmount);
    }
    //?}

    @Definition(id = "is", method = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    @Definition(id = "IS_PROJECTILE", field = "Lnet/minecraft/tags/DamageTypeTags;IS_PROJECTILE:Lnet/minecraft/tags/TagKey;")
    @Expression("?.is(IS_PROJECTILE)")
    @ModifyExpressionValue(method = "hurt", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$hurt$shield$knockback(boolean original) {
        if (!(tacztweaks$customShieldResult instanceof CustomShieldResult.Blocked blocked)) return original;
        return blocked.knockback();
    }
}