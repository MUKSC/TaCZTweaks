package me.muksc.tacztweaks.mixin.feature.balancing.rpm;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.resource.modifier.custom.RpmModifier;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RpmModifier.class, remap = false)
public abstract class RpmModifierMixin {
    @ModifyExpressionValue(method = "initCache", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunData;getRoundsPerMinute(Lcom/tacz/guns/api/item/gun/FireMode;)I"))
    private int tacztweaks$initCache$rpmModifier(int original) {
        return (int) Config.Balancing.RPM.eval(original);
    }

    //~ environment environment_client
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunData;getRoundsPerMinute(Lcom/tacz/guns/api/item/gun/FireMode;)I"))
    private int tacztweaks$getPropertyDiagramsData$rpmModifier(int original) {
        return (int) Config.Balancing.RPM.eval(original);
    }
}