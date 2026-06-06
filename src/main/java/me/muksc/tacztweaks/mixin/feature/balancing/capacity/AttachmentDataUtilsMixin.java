package me.muksc.tacztweaks.mixin.feature.balancing.capacity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.util.AttachmentDataUtils;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AttachmentDataUtils.class, remap = false)
public abstract class AttachmentDataUtilsMixin {
    @ModifyReturnValue(method = "getAmmoCountWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)I", at = @At("RETURN"))
    private static int tacztweaks$getAmmoCountWithAttachmentWithAttachment$capacityModifier(int original) {
        return (int) Config.Balancing.Capacity.eval(original);
    }
}