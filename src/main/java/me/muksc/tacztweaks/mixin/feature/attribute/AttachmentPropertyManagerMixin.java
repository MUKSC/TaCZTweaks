package me.muksc.tacztweaks.mixin.feature.attribute;

import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import me.muksc.tacztweaks.feature.attribute.AttributeManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AttachmentPropertyManager.class, remap = false)
public abstract class AttachmentPropertyManagerMixin {
    @Inject(method = "lambda$postChangeEvent$1", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/event/ChangeGunPropertyEvent;internalOnAttachmentPropertyEvent(Lcom/tacz/guns/api/event/common/AttachmentPropertyEvent;)V", shift = At.Shift.AFTER))
    //? if forge || fabric {
    private static void tacztweaks$postChangeEvent$attribute$onPropertyUpdated(
        ItemStack gunItem, LivingEntity shooter, IGun iGun, CommonGunIndex index, CallbackInfo ci,
        @Local AttachmentCacheProperty cacheProperty
    ) {
    //?} else if neoforge {
    /*private static void tacztweaks$postChangeEvent$onPropertyUpdated(
        LivingEntity shooter, ItemStack gunItem, IGun iGun, CommonGunIndex index, CallbackInfo ci,
        @Local AttachmentCacheProperty cacheProperty
    ) {
    *///?}
        AttributeManager.INSTANCE.onPropertyUpdated(shooter, gunItem, cacheProperty);
    }

    @Inject(method = "postChangeEvent", at = @At("HEAD"))
    private static void tacztweaks$postChangeEvent$attribute$onPropertyReset(LivingEntity shooter, ItemStack gunItem, CallbackInfo ci) {
        if (gunItem.getItem() instanceof IGun) return;
        AttributeManager.INSTANCE.onPropertyUpdated(shooter, gunItem, null);
    }
}