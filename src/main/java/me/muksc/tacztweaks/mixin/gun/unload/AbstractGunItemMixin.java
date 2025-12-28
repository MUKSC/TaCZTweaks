package me.muksc.tacztweaks.mixin.gun.unload;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.index.CommonAmmoIndex;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.mixininterface.gun.unload.UnloadableAbstractGunItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @WrapOperation(method = "lambda$dropAllAmmo$3", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/gun/AbstractGunItem;setCurrentAmmoCount(Lnet/minecraft/world/item/ItemStack;I)V"))
    private void tacztweaks$dropAllAmmo$unloadOneInTheChamber(AbstractGunItem instance, ItemStack itemStack, int i, Operation<Void> original) {
        original.call(instance, itemStack, i);
        if (!tacztweaks$unloading || i != 0) return;
        if (Config.Gun.INSTANCE.unloadBulletInBarrel()) instance.setBulletInBarrel(itemStack, false);
    }

    @Inject(method = "lambda$dropAllAmmo$2", at = @At("TAIL"))
    private void tacztweaks$dropAll$unloadOneInTheChamberAndGive(int ammoCount, ResourceLocation ammoId, Player player, ItemStack gunItem, CommonAmmoIndex ammoIndex, CallbackInfo ci) {
        if (!tacztweaks$unloading) return;
        var instance = AbstractGunItem.class.cast(this);
        if (!Config.Gun.INSTANCE.unloadBulletInBarrel() || !instance.hasBulletInBarrel(gunItem)) return;
        instance.setBulletInBarrel(gunItem, false);
        ItemHandlerHelper.giveItemToPlayer(player, AmmoItemBuilder.create().setId(ammoId).setCount(1).build());
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
