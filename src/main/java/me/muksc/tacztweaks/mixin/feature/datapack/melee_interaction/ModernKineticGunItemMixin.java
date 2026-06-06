package me.muksc.tacztweaks.mixin.feature.datapack.melee_interaction;

import com.google.common.base.Supplier;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.pojo.data.attachment.EffectData;
import me.muksc.tacztweaks.feature.datapack.legacy.manager.MeleeInteractionManager;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ModernKineticGunItem.class, remap = false)
public abstract class ModernKineticGunItemMixin {
    @Definition(id = "realDamage", local = @Local(type = java.util.function.Supplier.class, name = "realDamage"))
    @Definition(id = "memoize", method = "Lcom/google/common/base/Suppliers;memoize(Lcom/google/common/base/Supplier;)Lcom/google/common/base/Supplier;")
    @Expression("realDamage = @(memoize(?))")
    @ModifyExpressionValue(method = "doMelee", at = @At("MIXINEXTRAS:EXPRESSION"))
    private Supplier<Float> tacztweaks$doMelee$initialize(
        Supplier<Float> original,
        @Share("realDamage") LocalRef<Supplier<Float>> realDamageRef,
        @Share("hit") LocalBooleanRef hitRef
    ) {
        realDamageRef.set(original);
        hitRef.set(false);
        return original;
    }

    @Inject(method = "doMelee", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/item/ModernKineticGunItem;doPerLivingHurt(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;FFLjava/util/List;)V"))
    private void tacztweaks$doMelee$setHit(
        LivingEntity user, float gunDistance, float meleeDistance, float rangeAngle, float knockback, float damage, List<EffectData> effects, CallbackInfo ci,
        @Share("hit") LocalBooleanRef hitRef
    ) {
        hitRef.set(true);
    }

    @Inject(method = "doMelee", at = @At("TAIL"))
    private void tacztweaks$doMelee$handle(
        LivingEntity user, float gunDistance, float meleeDistance, float rangeAngle, float knockback, float damage, List<EffectData> effects, CallbackInfo ci,
        @Share("realDamage") LocalRef<Supplier<Float>> realDamageRef,
        @Share("hit") LocalBooleanRef hitRef
    ) {
        if (hitRef.get()) return;
        MeleeInteractionManager.handleBlockInteraction(user, 1 + gunDistance + meleeDistance, realDamageRef.get().get());
    }
}