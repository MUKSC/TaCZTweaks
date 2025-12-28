package me.muksc.tacztweaks.mixin.features.bullet_interactions;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.tacz.guns.init.ModDamageTypes;
import me.muksc.tacztweaks.mixininterface.features.bullet_interaction.ShieldInteractionBehaviour;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ShieldInteractionBehaviour {
    @Unique
    private Function<Integer, Integer> tacztweaks$customShieldDurabilityDamage = null;

    @Override
    public Function<Integer, Integer> tacztweaks$getCustomShieldDurabilityDamage() {
        return tacztweaks$customShieldDurabilityDamage;
    }

    @Unique
    private Integer tacztweaks$customShieldDisableDuration = null;

    @Override
    public void tacztweaks$setCustomShieldDurabilityDamage(Function<Integer, Integer> damage) {
        tacztweaks$customShieldDurabilityDamage = damage;
    }

    @Override
    public Integer tacztweaks$getCustomShieldDisableDuration() {
        return tacztweaks$customShieldDisableDuration;
    }

    @Override
    public void tacztweaks$setCustomShieldDisableDuration(Integer duration) {
        tacztweaks$customShieldDisableDuration = duration;
    }

    @WrapMethod(method = "hurt")
    private boolean tacztweaks$hurt$resetShieldInteractionContext(DamageSource pSource, float pAmount, Operation<Boolean> original) {
        try {
            return original.call(pSource, pAmount);
        } finally {
            tacztweaks$customShieldDurabilityDamage = null;
            tacztweaks$customShieldDisableDuration = null;
        }
    }

    @Unique
    private boolean tacztweaks$handlingBullets = false;

    @WrapMethod(method = "hurt")
    private boolean tacztweaks$hurt$checkBullet(DamageSource pSource, float pAmount, Operation<Boolean> original) {
        try {
            if (pSource.is(ModDamageTypes.BULLETS_TAG)) tacztweaks$handlingBullets = true;
            return original.call(pSource, pAmount);
        } finally {
            tacztweaks$handlingBullets = false;
        }
    }

    @Definition(id = "is", method = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    @Definition(id = "BYPASSES_SHIELD", field = "Lnet/minecraft/tags/DamageTypeTags;BYPASSES_SHIELD:Lnet/minecraft/tags/TagKey;")
    @Expression("?.is(BYPASSES_SHIELD)")
    @ModifyExpressionValue(method = "isDamageSourceBlocked", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$isDamageSourceBlocked$blockableBullets(boolean original, @Local(argsOnly = true, ordinal = 0) DamageSource pDamageSource) {
        if (!tacztweaks$handlingBullets) return original;
        return false;
    }

    @Definition(id = "is", method = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    @Definition(id = "IS_PROJECTILE", field = "Lnet/minecraft/tags/DamageTypeTags;IS_PROJECTILE:Lnet/minecraft/tags/TagKey;")
    @Expression("?.is(IS_PROJECTILE)")
    @WrapOperation(method = "hurt", at = @At("MIXINEXTRAS:EXPRESSION"), require = /* Arclight */ 0)
    private boolean tacztweaks$hurt$customShieldDisable(DamageSource instance, TagKey<DamageType> pDamageTypeKey, Operation<Boolean> original) {
        if (!tacztweaks$handlingBullets) return original.call(instance, pDamageTypeKey);
        var entity = LivingEntity.class.cast(this);
        if (!(entity instanceof Player player)) return original.call(instance, pDamageTypeKey);
        if (tacztweaks$customShieldDisableDuration == null) return original.call(instance, pDamageTypeKey);
        if (tacztweaks$customShieldDisableDuration > 0) player.disableShield(false);
        return true;
    }

    @Definition(id = "pAmount", local = @Local(type = float.class, argsOnly = true, ordinal = 0))
    @Expression("pAmount <= 0.0")
    @WrapOperation(method = "hurt", at = @At("MIXINEXTRAS:EXPRESSION"), require = /* Arclight */ 0)
    private boolean tacztweaks$hurt$alwaysPlayBlockingSound(float left, float right, Operation<Boolean> original, @Share("originalAmount") LocalFloatRef originalAmountRef) {
        if (!tacztweaks$handlingBullets) return original.call(left, right);
        return true;
    }

    @Definition(id = "useItemRemaining", field = "Lnet/minecraft/world/entity/LivingEntity;useItemRemaining:I")
    @Expression("? - this.useItemRemaining >= 5")
    @WrapOperation(method = "isBlocking", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$isBlocking$bypassCooldown(int left, int right, Operation<Boolean> original) {
        if (!tacztweaks$handlingBullets) return original.call(left, right);
        return true;
    }
}
