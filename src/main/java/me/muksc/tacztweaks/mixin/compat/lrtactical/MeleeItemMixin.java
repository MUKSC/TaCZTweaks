package me.muksc.tacztweaks.mixin.compat.lrtactical;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import me.muksc.tacztweaks.data.manager.MeleeInteractionManager;
import me.xjqsh.lrtactical.api.melee.MeleeAction;
import me.xjqsh.lrtactical.item.MeleeItem;
import me.xjqsh.lrtactical.item.index.MeleeWeaponIndex;
import me.xjqsh.lrtactical.item.melee.CombatData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = MeleeItem.class, remap = false)
public abstract class MeleeItemMixin {
    @Definition(id = "attackInfo", local = @Local(type = CombatData.MeleeAttackInfo.class))
    @Definition(id = "getFactor", method = "Lme/xjqsh/lrtactical/item/melee/CombatData$MeleeAttackInfo;getFactor()F")
    @Expression("? * attackInfo.getFactor()")
    @ModifyExpressionValue(method = "lambda$attack$10", at = @At("MIXINEXTRAS:EXPRESSION"))
    private float tacztweaks$attack$initialize(
        float original,
        @Local(argsOnly = true) Player attacker,
        @Local(argsOnly = true) List<Entity> targets,
        @Share("damage") LocalFloatRef damageRef,
        @Share("hit") LocalBooleanRef hitRef
    ) {
        damageRef.set(original);
        hitRef.set(false);
        return original;
    }

    @Inject(method = "lambda$attack$10", at = @At(value = "INVOKE", target = "Lme/xjqsh/lrtactical/item/MeleeItem;performAttack(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;FF)Lme/xjqsh/lrtactical/api/melee/AttackResult;"))
    private void tacztweaks$doMelee$setHit(
        MeleeAction action,
        Player attacker,
        float base,
        List<Entity> targets,
        ItemStack stack,
        MeleeWeaponIndex<?> index,
        CallbackInfo ci,
        @Share("hit") LocalBooleanRef hitRef
    ) {
        hitRef.set(true);
    }

    @Inject(method = "lambda$attack$10", at = @At("TAIL"))
    private void tacztweaks$attack$handle(
        MeleeAction action,
        Player attacker,
        float base,
        List<Entity> targets,
        ItemStack stack,
        MeleeWeaponIndex<?> index,
        CallbackInfo ci,
        @Local CombatData.MeleeAttackInfo attackInfo,
        @Share("damage") LocalFloatRef damageRef,
        @Share("hit") LocalBooleanRef hitRef
    ) {
        if (!(attacker instanceof ServerPlayer player)) return;
        if (hitRef.get()) return;
        MeleeInteractionManager.INSTANCE.handleBlockInteraction(player, attackInfo.getHitbox().getMaxRange(), damageRef.get());
    }
}
