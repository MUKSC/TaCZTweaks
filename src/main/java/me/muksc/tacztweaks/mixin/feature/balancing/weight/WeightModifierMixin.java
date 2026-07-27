package me.muksc.tacztweaks.mixin.feature.balancing.weight;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.custom.WeightModifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = WeightModifier.class, remap = false)
public abstract class WeightModifierMixin {
    @ModifyExpressionValue(method = "initCache", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunData;getWeight()F"))
    private float tacztweaks$initCache$weightModifier(float original) {
        return (float) Config.Balancing.Weight.eval(original);
    }

    //~ environment environment_client
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    @Inject(method = "getPropertyDiagramsData", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;"))
    private void tacztweaks$getPropertyDiagramsData$weightModifier(
        ItemStack gunItem, GunData gunData, AttachmentCacheProperty cacheProperty, CallbackInfoReturnable<List<Object>> cir,
        @Local(name = "weight") LocalFloatRef weightRef
    ) {
        weightRef.set((float) Config.Balancing.Weight.eval(weightRef.get()));
    }
}