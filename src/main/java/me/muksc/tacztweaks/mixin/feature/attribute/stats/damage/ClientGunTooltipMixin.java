package me.muksc.tacztweaks.mixin.feature.attribute.stats.damage;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.tooltip.ClientGunTooltip;
import me.muksc.tacztweaks.core.extension.DeferredHolderExt;
import me.muksc.tacztweaks.registry.ModAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ClientGunTooltip.class, remap = false)
public abstract class ClientGunTooltipMixin {
    @ModifyExpressionValue(method = "getText", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/AttachmentDataUtils;getDamageWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)D"))
    private double tacztweaks$getText$attribute$stats$damage(double original) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return original;
        AttributeInstance attribute = player.getAttribute(DeferredHolderExt.valueOrDelegate(ModAttributes.DAMAGE));
        if (attribute == null || attribute.getModifiers().isEmpty()) return original;
        double originalBaseValue = attribute.getBaseValue();
        try {
            attribute.setBaseValue(original);
            return attribute.getValue();
        } finally {
            attribute.setBaseValue(originalBaseValue);
        }
    }
}