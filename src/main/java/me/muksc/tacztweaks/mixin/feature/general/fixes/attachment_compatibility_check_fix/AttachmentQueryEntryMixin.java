package me.muksc.tacztweaks.mixin.feature.general.fixes.attachment_compatibility_check_fix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.compat.jei.entry.AttachmentQueryEntry;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AttachmentQueryEntry.class, remap = false)
public abstract class AttachmentQueryEntryMixin {
    @WrapOperation(method = "lambda$addAllAllowGuns$1", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;allowAttachment(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean tacztweaks$addAllAllowGuns$attachmentCompatibilityCheckFix(IGun instance, ItemStack gun, ItemStack attachmentItem, Operation<Boolean> original) {
        if (!Config.General.Fixes.attachmentCompatibilityCheckFix()) return original.call(instance, gun, attachmentItem);
        IAttachment attachment = IAttachment.getIAttachmentOrNull(attachmentItem);
        if (attachment == null) return original.call(instance, gun, attachmentItem);
        return original.call(instance, gun, attachmentItem)
            && instance.allowAttachmentType(gun, attachment.getType(attachmentItem));
    }
}