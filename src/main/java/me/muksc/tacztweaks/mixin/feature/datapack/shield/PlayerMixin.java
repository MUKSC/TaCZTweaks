package me.muksc.tacztweaks.mixin.feature.datapack.shield;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.muksc.tacztweaks.feature.datapack.shield.CustomShieldResult;
import me.muksc.tacztweaks.mixininterface.feature.datapack.shield.CustomShieldEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Player.class)
public abstract class PlayerMixin implements CustomShieldEntity {
    //? if forge {
    @ModifyExpressionValue(method = "blockUsingShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;canDisableShield(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z", remap = false))
    //?} else {
    /*@ModifyExpressionValue(method = "blockUsingShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;canDisableShield()Z"))
    *///?}
    private boolean tacztweaks$blockUsingShield$shield$disable(boolean original) {
        return original || tacztweaks$getShieldResult() instanceof CustomShieldResult.Blocked;
    }

    //? if <1.20.5 {
    @ModifyExpressionValue(method = "disableShield", at = @At(value = "CONSTANT", args = "floatValue=0.25"))
    private float tacztweaks$disableShield$shield$disable$alwaysDisable(float original) {
        if (!(tacztweaks$getShieldResult() instanceof CustomShieldResult.Blocked)) return original;
        return 1.0F;
    }
    //?}

    @ModifyArg(method = "disableShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemCooldowns;addCooldown(Lnet/minecraft/world/item/Item;I)V"), index = 1)
    private int tacztweaks$disableShield$shield$disable$modifyDuration(int ticks) {
        if (!(tacztweaks$getShieldResult() instanceof CustomShieldResult.Blocked blocked)) return ticks;
        return blocked.disableDuration();
    }
}