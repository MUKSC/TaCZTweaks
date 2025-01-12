package me.muksc.tacztweaks.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.gui.GunSmithTableScreen;
import com.tacz.guns.client.resource.pojo.PackInfo;
import com.tacz.guns.crafting.GunSmithTableIngredient;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.inventory.GunSmithTableMenu;
import me.muksc.tacztweaks.UtilKt;
import me.muksc.tacztweaks.client.Recipes;
import me.muksc.tacztweaks.client.gui.PackFilterWidget;
import me.muksc.tacztweaks.client.gui.SearchWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(value = GunSmithTableScreen.class, remap = false)
public abstract class GunSmithTableScreenMixin extends AbstractContainerScreen<GunSmithTableMenu> {
    @Shadow private int indexPage;
    @Shadow private String selectedType;
    @Shadow private List<ResourceLocation> selectedRecipeList;
    @Shadow @Nullable private GunSmithTableRecipe selectedRecipe;
    @Shadow @Final private Map<String, List<ResourceLocation>> recipes;
    @Shadow(remap = true) protected abstract void init();
    @Shadow @Nullable protected abstract GunSmithTableRecipe getSelectedRecipe(ResourceLocation recipeId);

    public GunSmithTableScreenMixin(GunSmithTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Unique @Nullable
    private SearchWidget tacztweaks$searchBar = null;

    @Unique @Nullable
    private PackFilterWidget tacztweaks$packFilter = null;

    @Unique
    private Map<String, PackInfo> tacztweaks$packs = Collections.emptyMap();

    @Unique
    private Map<ResourceLocation, String> tacztweaks$idToPackId = Collections.emptyMap();

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (tacztweaks$searchBar != null && tacztweaks$searchBar.mouseClicked(pMouseX, pMouseY, pButton)) return true;
        if (tacztweaks$packFilter != null && tacztweaks$packFilter.mouseClicked(pMouseX, pMouseY, pButton)) return true;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (tacztweaks$packFilter != null && tacztweaks$packFilter.mouseScrolled(pMouseX, pMouseY, pDelta)) return true;
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        boolean canSearch = pKeyCode != 256 && tacztweaks$searchBar != null;
        if (canSearch && (tacztweaks$searchBar.keyPressed(pKeyCode, pScanCode, pModifiers) || tacztweaks$searchBar.canConsumeInput())) return true;
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (tacztweaks$searchBar != null && tacztweaks$searchBar.charTyped(pCodePoint, pModifiers)) return true;
        return super.charTyped(pCodePoint, pModifiers);
    }

    @Inject(method = "init", at = @At("TAIL"), remap = true)
    private void init$addWidgets(CallbackInfo ci) {
        if (tacztweaks$searchBar == null) {
            tacztweaks$searchBar = new SearchWidget(font, 0, 0, 206, 18);
            tacztweaks$searchBar.setResponder(query -> init());
        }
        tacztweaks$searchBar.setX(leftPos + 137);
        tacztweaks$searchBar.setY(topPos + 190);
        addRenderableOnly(tacztweaks$searchBar);

        if (tacztweaks$packFilter == null) {
            tacztweaks$packFilter = new PackFilterWidget(tacztweaks$packs, font, 0, 0,  0, 205);
            tacztweaks$packFilter.setOnFilterChanged((index, filter) -> init());
        }
        tacztweaks$packFilter.setX(leftPos + 346);
        tacztweaks$packFilter.setY(topPos + 4);
        tacztweaks$packFilter.setWidth(width - (leftPos + 346) - 5);
        addRenderableOnly(tacztweaks$packFilter);
    }

    @Inject(method = "init", at = @At("HEAD"), remap = true)
    private void init$filterBySearchQuery(CallbackInfo ci) {
        String query = tacztweaks$searchBar != null ? tacztweaks$searchBar.getValue().toLowerCase() : "";
        selectedRecipeList = recipes.get(selectedType).stream().filter(recipeId -> {
            MutableBoolean flag = new MutableBoolean(false);
            Optional.ofNullable(getSelectedRecipe(recipeId)).ifPresent(recipe -> {
                String name = recipe.getOutput().getHoverName().getString().toLowerCase();
                if (query.isEmpty() || name.contains(query)) flag.setTrue();

                ResourceLocation id = UtilKt.getTaCZId(recipe.getOutput());
                if (id == null || tacztweaks$packFilter == null || !tacztweaks$packFilter.shouldFilter()) return;
                String packId = tacztweaks$idToPackId.get(id);
                if (packId == null) return;
                flag.setValue(flag.getValue() && tacztweaks$packFilter.include(packId));
            });
            return flag.getValue();
        }).toList();
        if (selectedRecipe != null && !selectedRecipeList.contains(selectedRecipe.getId())) {
            if (selectedRecipeList.isEmpty()) {
                selectedRecipe = null;
            } else {
                selectedRecipe = getSelectedRecipe(selectedRecipeList.get(0));
            }
        }
        int lastIndexPage = (selectedRecipeList.size() - 1) / 6;
        if (indexPage > lastIndexPage) indexPage = lastIndexPage;
    }

    @ModifyExpressionValue(method = "lambda$addCraftButton$3", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/crafting/GunSmithTableRecipe;getInputs()Ljava/util/List;"))
    private List<GunSmithTableIngredient> addCraftButton$creativeCraft(List<GunSmithTableIngredient> original) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isCreative()) return original;
        return Collections.emptyList();
    }

    @ModifyArg(method = "renderIngredient", at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"), index = 1)
    private Object[] renderIngredient$creativeCraft$modifyDisplayCount(Object[] args) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isCreative()) return args;
        args[1] = 9999;
        return args;
    }

    @ModifyArg(method = "renderIngredient", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", remap = true), index = 4)
    private int renderIngredient$creativeCraft$modifyColor(int color) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isCreative()) return color;
        return 16777215;
    }

    @ModifyExpressionValue(method = "classifyRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getAllRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;)Ljava/util/List;", remap = true))
    private List<GunSmithTableRecipe> classifyRecipes$filterRecipes(List<GunSmithTableRecipe> original) {
        Recipes recipes =  Recipes.Companion.getRecipes(original);
        tacztweaks$packs = recipes.getPacks();
        tacztweaks$idToPackId = recipes.getIdToPackId();
        return recipes.getRecipes();
    }
}
