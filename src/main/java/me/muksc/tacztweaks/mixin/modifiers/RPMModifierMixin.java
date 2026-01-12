package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.modifier.custom.RpmModifier;
import me.muksc.tacztweaks.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RpmModifier.class, remap = false)
public abstract class RPMModifierMixin {
    @ModifyExpressionValue(method = "initCache", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunData;getRoundsPerMinute(Lcom/tacz/guns/api/item/gun/FireMode;)I"))
    private int tacztweaks$initCache$rpmModifier(int original) {
        return (int) AttachmentPropertyManager.eval(Config.Modifiers.RPM.INSTANCE.toTaCZ(), original);
    }

    @OnlyIn(Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunData;getRoundsPerMinute(Lcom/tacz/guns/api/item/gun/FireMode;)I"))
    private int tacztweaks$getPropertyDiagramsData$rpmModifier(int original) {
        return (int) AttachmentPropertyManager.eval(Config.Modifiers.RPM.INSTANCE.toTaCZ(), original);
    }
}
