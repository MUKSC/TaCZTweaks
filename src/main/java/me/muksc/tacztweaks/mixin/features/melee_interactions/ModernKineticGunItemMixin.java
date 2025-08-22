package me.muksc.tacztweaks.mixin.features.melee_interactions;

import com.google.common.base.Supplier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.pojo.data.attachment.EffectData;
import me.muksc.tacztweaks.data.MeleeInteractionManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ModernKineticGunItem.class, remap = false)
public abstract class ModernKineticGunItemMixin {
    @ModifyExpressionValue(method = "doMelee", at = @At(value = "INVOKE", target = "Lcom/google/common/base/Suppliers;memoize(Lcom/google/common/base/Supplier;)Lcom/google/common/base/Supplier;"))
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
        LivingEntity user,
        float gunDistance,
        float meleeDistance,
        float rangeAngle,
        float knockback,
        float damage,
        List<EffectData> effects,
        CallbackInfo ci,
        @Share("hit") LocalBooleanRef hitRef
    ) {
        hitRef.set(true);
    }

    @Inject(method = "doMelee", at = @At("TAIL"))
    private void tacztweaks$doMelee$handle(
        LivingEntity user,
        float gunDistance,
        float meleeDistance,
        float rangeAngle,
        float knockback,
        float damage,
        List<EffectData> effects,
        CallbackInfo ci,
        @Share("realDamage") LocalRef<Supplier<Float>> realDamageRef,
        @Share("hit") LocalBooleanRef hitRef
    ) {
        if (!(user instanceof ServerPlayer player)) return;
        if (hitRef.get()) return;
        MeleeInteractionManager.INSTANCE.handleBlockInteraction(player, 1 + gunDistance + meleeDistance, realDamageRef.get().get());
    }
}
