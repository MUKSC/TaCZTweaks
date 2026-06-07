package me.muksc.tacztweaks.mixin.feature.general.fixes.crashes;

import com.tacz.guns.client.gui.GunSmithTableScreen;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GunSmithTableScreen.class)
public abstract class GunSmithTableScreenMixin extends AbstractContainerScreen<GunSmithTableMenu> {
    @Shadow(remap = false) private List<ResourceLocation> selectedRecipeList;

    public GunSmithTableScreenMixin(GunSmithTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    //? if <1.20.2 {
    private void tacztweaks$mouseScrolled$fixNPE(double pMouseX, double pMouseY, double pDelta, CallbackInfoReturnable<Boolean> cir) {
    //?} else {
    /*private void tacztweaks$mouseScrolled$fixNPE(double pMouseX, double pMouseY, double pScrollX, double pScrollY, CallbackInfoReturnable<Boolean> cir) {
    *///?}
        if (selectedRecipeList != null) return;
        //? if <1.20.2 {
        cir.setReturnValue(super.mouseScrolled(pMouseX, pMouseY, pDelta));
        //?} else {
        /*cir.setReturnValue(super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY));
        *///?}
    }
}