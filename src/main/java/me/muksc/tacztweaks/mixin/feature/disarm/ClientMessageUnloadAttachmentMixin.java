package me.muksc.tacztweaks.mixin.feature.disarm;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.network.message.ClientMessageUnloadAttachment;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ClientMessageUnloadAttachment.class, remap = false)
public abstract class ClientMessageUnloadAttachmentMixin {
    //? if forge {
    @ModifyExpressionValue(method = "lambda$handle$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;hasAttachmentLock(Lnet/minecraft/world/item/ItemStack;)Z"))
    //?} else if fabric && 1.20.1 {
    /*@ModifyExpressionValue(method = "handle", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;hasAttachmentLock(Lnet/minecraft/world/item/ItemStack;)Z"))
    *///?} else if (fabric && 1.21.1) || neoforge {
    /*@ModifyExpressionValue(method = "lambda$handle$2", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;hasAttachmentLock(Lnet/minecraft/world/item/ItemStack;)Z"))
    *///?}
    private static boolean tacztweaks$handle$disarm(boolean original) {
        return original || DisarmManager.shouldDisarm();
    }
}