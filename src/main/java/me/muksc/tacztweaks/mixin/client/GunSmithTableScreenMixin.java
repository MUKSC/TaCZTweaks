package me.muksc.tacztweaks.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.gui.GunSmithTableScreen;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.inventory.GunSmithTableMenu;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Debug(export = true)
@Mixin(value = GunSmithTableScreen.class, remap = false)
public abstract class GunSmithTableScreenMixin extends AbstractContainerScreen<GunSmithTableMenu> {
    @Shadow private List<ResourceLocation> selectedRecipeList;

    @Shadow @Nullable private Int2IntArrayMap playerIngredientCount;

    @Shadow @Nullable private GunSmithTableRecipe selectedRecipe;

    public GunSmithTableScreenMixin(GunSmithTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @ModifyExpressionValue(method = "classifyRecipes", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/TimelessAPI;getAllRecipes()Ljava/util/Map;"))
    private Map<ResourceLocation, GunSmithTableRecipe> classifyRecipes$filterRecipes(Map<ResourceLocation, GunSmithTableRecipe> original) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return original;

        ItemStack gunStack = player.getMainHandItem();
        if (gunStack.isEmpty()) return original;

        IGun gun = IGun.getIGunOrNull(gunStack);
        if (gun == null) return original;

        return original.entrySet().stream()
            .filter(entry -> {
                GunSmithTableRecipe recipe = entry.getValue();

                IAttachment attachment = IAttachment.getIAttachmentOrNull(recipe.getOutput());
                if (attachment != null) {
                    return gun.allowAttachment(gunStack, recipe.getOutput());
                }
                IAmmo ammo = IAmmo.getIAmmoOrNull(recipe.getOutput());
                if (ammo != null) {
                    return ammo.isAmmoOfGun(gunStack, recipe.getOutput());
                }
                return false;
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private Object init$selectedRecipeList$nullIfNullOrEmpty(List<ResourceLocation> instance, int i, Operation<Object> original) {
        if (instance != null && !instance.isEmpty()) return original.call(instance, i);
        return null;
    }

    @WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gui/GunSmithTableScreen;getPlayerIngredientCount(Lcom/tacz/guns/crafting/GunSmithTableRecipe;)V"))
    private boolean init$playerIngredientCount$nullIfNullOrEmpty(GunSmithTableScreen instance, GunSmithTableRecipe ingredient) {
        if (selectedRecipeList != null && !selectedRecipeList.isEmpty()) return true;
        playerIngredientCount = null;
        return false;
    }

    @WrapOperation(method = "addTypeButtons", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean addTypeButtons$trueIfNull(List<ResourceLocation> instance, Operation<Boolean> original) {
        if (instance != null) return original.call(instance);
        return true;
    }

    @WrapOperation(method = "lambda$addTypeButtons$7", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private Object addTypeButtons$selectedRecipeList$nullIfNullOrEmpty(List<ResourceLocation> instance, int i, Operation<Object> original) {
        if (instance != null && !instance.isEmpty()) return original.call(instance, i);
        return null;
    }

    @WrapWithCondition(method = "lambda$addTypeButtons$7", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gui/GunSmithTableScreen;getPlayerIngredientCount(Lcom/tacz/guns/crafting/GunSmithTableRecipe;)V"))
    private boolean addTypeButtons$playerIngredientCount$nullIfNullOrEmpty(GunSmithTableScreen instance, GunSmithTableRecipe ingredient) {
        if (selectedRecipeList != null && !selectedRecipeList.isEmpty()) return true;
        playerIngredientCount = null;
        return false;
    }
}
