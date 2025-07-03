package me.muksc.tacztweaks.mixin.gun.unload;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import me.muksc.tacztweaks.mixininterface.gun.unload.UnloadableAbstractGunItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AbstractGunItem.class, remap = false)
public abstract class AbstractGunItemMixin implements UnloadableAbstractGunItem {
    @Unique
    private boolean tacztweaks$unloading = false;

    @Override
    public void tacztweaks$setUnloading() {
        tacztweaks$unloading = true;
    }

    @ModifyExpressionValue(method = "lambda$dropAllAmmo$3", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isCreative()Z", remap = true))
    private boolean tacztweaks$dropAllAmmo$unload(boolean original) {
        return !tacztweaks$unloading && original;
    }

    @WrapWithCondition(method = "lambda$dropAllAmmo$2", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"))
    private boolean tacztweaks$dropAllAmmo$giveItemsOnlyIf(Player player, ItemStack stack) {
        return !player.isCreative();
    }

    @WrapMethod(method = "dropAllAmmo")
    private void tacztweaks$dropAllAmmo$resetUnloading(Player player, ItemStack gunItem, Operation<Void> original) {
        try {
            original.call(player, gunItem);
        } finally {
            tacztweaks$unloading = false;
        }
    }
}
