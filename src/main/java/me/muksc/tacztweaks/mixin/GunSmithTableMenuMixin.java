package me.muksc.tacztweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.crafting.GunSmithTableIngredient;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Mixin(value = GunSmithTableMenu.class, remap = false)
public abstract class GunSmithTableMenuMixin {
    @ModifyExpressionValue(method = "lambda$doCraft$1", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/crafting/GunSmithTableRecipe;getInputs()Ljava/util/List;"))
    private List<GunSmithTableIngredient> creativeCraft(List<GunSmithTableIngredient> original, @Local(argsOnly = true) Player player) {
        if (!player.isCreative()) return original;
        return Collections.emptyList();
    }
}
