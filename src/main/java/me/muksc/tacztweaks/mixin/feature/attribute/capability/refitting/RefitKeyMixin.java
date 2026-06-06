package me.muksc.tacztweaks.mixin.feature.attribute.capability.refitting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.client.input.RefitKey;
import me.muksc.tacztweaks.core.extension.DeferredHolderExt;
import me.muksc.tacztweaks.registry.ModAttributes;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RefitKey.class, remap = false)
public abstract class RefitKeyMixin {
    @ModifyExpressionValue(method = "onRefitPress", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;hasAttachmentLock(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean tacztweaks$onRefitPress$attribute$capability$refitting(
        boolean original,
        @Local LocalPlayer player
    ) {
        double value = player.getAttributeValue(DeferredHolderExt.valueOrDelegate(ModAttributes.REFITTING));
        return original || value <= 0.0;
    }
}