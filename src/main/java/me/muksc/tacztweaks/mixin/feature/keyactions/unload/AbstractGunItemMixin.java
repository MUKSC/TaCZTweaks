package me.muksc.tacztweaks.mixin.feature.keyactions.unload;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AbstractGunItem.class, remap = false)
public abstract class AbstractGunItemMixin {
    @ModifyExpressionValue(method = "unloadAmmo(Lcom/tacz/guns/entity/shooter/ShooterDataHolder;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "CONSTANT", args = "intValue=1"))
    private int tacztweaks$unloadAmmo$unloadKey$unloadRoundInChamber(int original) {
        return Config.KeyActions.Unload.unloadRoundInChamber() ? original : 0;
    }
}