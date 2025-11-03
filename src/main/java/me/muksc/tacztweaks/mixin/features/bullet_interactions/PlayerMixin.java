package me.muksc.tacztweaks.mixin.features.bullet_interactions;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.muksc.tacztweaks.mixininterface.features.bullet_interaction.ShieldInteractionBehaviour;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements ShieldInteractionBehaviour {
    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Definition(id = "pDamage", local = @Local(type = float.class, argsOnly = true, ordinal = 0))
    @Expression("pDamage >= 3.0")
    @WrapOperation(method = "hurtCurrentlyUsedShield", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$hurtCurrentlyUsedShield$alwaysSucceed(float left, float right, Operation<Boolean> original) {
        if (tacztweaks$getCustomShieldDurabilityDamage() == null) return original.call(left, right);
        return true;
    }

    @Definition(id = "floor", method = "Lnet/minecraft/util/Mth;floor(F)I")
    @Definition(id = "pDamage", local = @Local(type = float.class, argsOnly = true, ordinal = 0))
    @Expression("1 + floor(pDamage)")
    @ModifyExpressionValue(method = "hurtCurrentlyUsedShield", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int tacztweaks$hurtCurrentlyUsedShield$customDamage(int original, @Share("damage") LocalRef<Integer> damageRef) {
        damageRef.set(null);
        if (tacztweaks$getCustomShieldDurabilityDamage() == null) return original;
        int damage = tacztweaks$getCustomShieldDurabilityDamage().apply(original);
        damageRef.set(damage);
        return damage;
    }

    @Inject(method = "hurtCurrentlyUsedShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getUsedItemHand()Lnet/minecraft/world/InteractionHand;"), cancellable = true)
    private void tacztweaks$hurtCurrentlyUsedShield$cancel(float pDamage, CallbackInfo ci, @Share("damage") LocalRef<Integer> damageRef) {
        Integer damage = damageRef.get();
        if (damage == null || damage > 0) return;
        ci.cancel();
    }

    @Definition(id = "nextFloat", method = "Lnet/minecraft/util/RandomSource;nextFloat()F")
    @Expression("?.nextFloat() < ?")
    @ModifyExpressionValue(method = "disableShield", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$disableShield$alwaysSucceed(boolean original) {
        if (tacztweaks$getCustomShieldDisableDuration() == null) return original;
        return true;
    }

    @ModifyArg(method = "disableShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemCooldowns;addCooldown(Lnet/minecraft/world/item/Item;I)V"), index = 1)
    private int tacztweaks$disableShield$customDuration(int pTicks) {
        if (tacztweaks$getCustomShieldDisableDuration() == null) return pTicks;
        return tacztweaks$getCustomShieldDisableDuration();
    }
}
