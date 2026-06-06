package me.muksc.tacztweaks.mixin.feature.attribute;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.muksc.tacztweaks.core.extension.DeferredHolderExt;
import me.muksc.tacztweaks.registry.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder tacztweaks$createLivingAttributes$attribute$appendAttributes(AttributeSupplier.Builder original) {
        for (var attribute : ModAttributes.ATTRIBUTES) {
            original.add(DeferredHolderExt.valueOrDelegate(attribute));
        }
        return original;
    }
}