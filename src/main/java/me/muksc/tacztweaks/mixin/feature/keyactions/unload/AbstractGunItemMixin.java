package me.muksc.tacztweaks.mixin.feature.keyactions.unload;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.index.CommonAmmoIndex;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.mixininterface.feature.keyactions.unload.UnloadableGun;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if forge {
import net.minecraftforge.items.ItemHandlerHelper;
//?} else if neoforge {
/*import net.neoforged.neoforge.items.ItemHandlerHelper;
*///?} else if fabric {
/*import cn.sh1rocu.tacz.util.itemhandler.ItemHandlerHelper;
*///?}

@Mixin(value = AbstractGunItem.class, remap = false)
public abstract class AbstractGunItemMixin implements UnloadableGun {
    @Shadow public abstract void dropAllAmmo(Player player, ItemStack gunItem);

    @Unique
    private boolean tacztweaks$unloading = false;

    @Override
    public void tacztweaks$unload(Player player, ItemStack gunItem) {
        try {
            tacztweaks$unloading = true;
            dropAllAmmo(player, gunItem);
        } finally {
            tacztweaks$unloading = false;
        }
    }

    @ModifyExpressionValue(method = "lambda$dropAllAmmo$3", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isCreative()Z", remap = true))
    private boolean tacztweaks$dropAllAmmo$unloadKey$disableCreativeReloadLogic(boolean original) {
        return !tacztweaks$unloading && original;
    }

    @Definition(id = "ammoCount", local = @Local(type = int.class, name = "ammoCount"))
    @Expression("ammoCount > 0")
    @ModifyExpressionValue(method = "dropAllAmmo", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$dropAllAmmo$unloadKey$unloadRoundInChamber$fixAmmoCountCheck(
        boolean original,
        @Local(argsOnly = true) ItemStack gunItem
    ) {
        AbstractGunItem instance = AbstractGunItem.class.cast(this);
        return original || (Config.KeyActions.Unload.unloadRoundInChamber() && instance.hasBulletInBarrel(gunItem));
    }

    @WrapOperation(method = "lambda$dropAllAmmo$2", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/builder/AmmoItemBuilder;setCount(I)Lcom/tacz/guns/api/item/builder/AmmoItemBuilder;"))
    private AmmoItemBuilder tacztweaks$dropAllAmmo$unloadKey$unloadRoundInChamber$storeCount(
        AmmoItemBuilder instance, int count, Operation<AmmoItemBuilder> original,
        @Share("count") LocalIntRef countRef
    ) {
        countRef.set(count);
        return original.call(instance, count);
    }

    //? if forge {
    @WrapWithCondition(method = "lambda$dropAllAmmo$2", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"))
    //?} else if neoforge {
    /*@WrapWithCondition(method = "lambda$dropAllAmmo$2", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"))
     *///?} else if fabric {
    /*@WrapWithCondition(method = "lambda$dropAllAmmo$2", at = @At(value = "INVOKE", target = "Lcn/sh1rocu/tacz/util/itemhandler/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"))
    *///?}
    private boolean tacztweaks$dropAllAmmo$unloadKey$unloadRoundInChamber$skipIfZero(
        Player player, ItemStack stack,
        @Share("count") LocalIntRef countRef
    ) {
        return countRef.get() != 0;
    }

    @WrapOperation(method = "lambda$dropAllAmmo$3", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/gun/AbstractGunItem;setCurrentAmmoCount(Lnet/minecraft/world/item/ItemStack;I)V"))
    private void tacztweaks$dropAllAmmo$unloadKey$unloadRoundInChamber$fuel(AbstractGunItem instance, ItemStack itemStack, int i, Operation<Void> original) {
        original.call(instance, itemStack, i);
        if (!Config.KeyActions.Unload.unloadRoundInChamber()) return;
        if (!tacztweaks$unloading || !instance.hasBulletInBarrel(itemStack) || i != 0) return;
        instance.setBulletInBarrel(itemStack, false);
    }

    //? if forge {
    @WrapWithCondition(method = "lambda$dropAllAmmo$2", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"))
    //?} else if neoforge {
    /*@WrapWithCondition(method = "lambda$dropAllAmmo$2", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"))
    *///?} else if fabric {
    /*@WrapWithCondition(method = "lambda$dropAllAmmo$2", at = @At(value = "INVOKE", target = "Lcn/sh1rocu/tacz/util/itemhandler/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"))
    *///?}
    private boolean tacztweaks$dropAllAmmo$unloadKey$cancelGivingItem(Player player, ItemStack stack) {
        return !tacztweaks$unloading || !player.isCreative();
    }

    @Inject(method = "lambda$dropAllAmmo$2", at = @At("TAIL"))
    private void tacztweaks$dropAll$unloadKey(int ammoCount, ResourceLocation ammoId, Player player, ItemStack gunItem, CommonAmmoIndex ammoIndex, CallbackInfo ci) {
        if (!Config.KeyActions.Unload.unloadRoundInChamber()) return;
        AbstractGunItem instance = AbstractGunItem.class.cast(this);
        if (!tacztweaks$unloading || !instance.hasBulletInBarrel(gunItem)) return;
        instance.setBulletInBarrel(gunItem, false);
        if (!player.isCreative()) ItemHandlerHelper.giveItemToPlayer(player, AmmoItemBuilder.create().setId(ammoId).setCount(1).build());
    }
}