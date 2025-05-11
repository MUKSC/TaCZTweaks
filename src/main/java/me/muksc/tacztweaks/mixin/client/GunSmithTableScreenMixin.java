package me.muksc.tacztweaks.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.gui.GunSmithTableScreen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GunSmithTableScreen.class, remap = false)
public abstract class GunSmithTableScreenMixin {
    @WrapOperation(method = "isSuitableForMainHand", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;allowAttachment(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean tacztweaks$isSuitableForMainHand$attachmentCheckFix(IGun instance, ItemStack gun, ItemStack attachmentItem, Operation<Boolean> original) {
        IAttachment iAttachment = IAttachment.getIAttachmentOrNull(attachmentItem);
        boolean allowAttachment = original.call(instance, gun, attachmentItem);
        if (iAttachment == null) return allowAttachment;
        return allowAttachment && instance.allowAttachmentType(gun, iAttachment.getType(attachmentItem));
    }
}
