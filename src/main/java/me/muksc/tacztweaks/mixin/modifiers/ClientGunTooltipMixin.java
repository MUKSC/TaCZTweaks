package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.tooltip.ClientGunTooltip;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import me.muksc.tacztweaks.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ClientGunTooltip.class, remap = false)
public abstract class ClientGunTooltipMixin {
    @Definition(id = "DAMAGE_BASE_MULTIPLIER", field = "Lcom/tacz/guns/config/sync/SyncConfig;DAMAGE_BASE_MULTIPLIER:Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;")
    @Definition(id = "get", method = "Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;get()Ljava/lang/Object;")
    @Definition(id = "Double", type = Double.class)
    @Expression("? * (Double) DAMAGE_BASE_MULTIPLIER.get()")
    @ModifyExpressionValue(method = "getText", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double tacztweaks$getText$damageModifier(double original) {
        return AttachmentPropertyManager.eval(Config.Modifiers.INSTANCE.damage(), original);
    }
}
