package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.modifier.custom.DamageModifier;
import me.muksc.tacztweaks.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = DamageModifier.class, remap = false)
public abstract class DamageModifierMixin {
    @Definition(id = "DAMAGE_BASE_MULTIPLIER", field = "Lcom/tacz/guns/config/sync/SyncConfig;DAMAGE_BASE_MULTIPLIER:Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;")
    @Definition(id = "get", method = "Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;get()Ljava/lang/Object;")
    @Definition(id = "Double", type = Double.class)
    @Expression("? * (Double) DAMAGE_BASE_MULTIPLIER.get()")
    @ModifyExpressionValue(method = "initCache", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double tacztweaks$initCache$damageModifier(double original) {
        return AttachmentPropertyManager.eval(Config.Modifiers.INSTANCE.damage(), original);
    }

    @OnlyIn(Dist.CLIENT)
    @Definition(id = "DAMAGE_BASE_MULTIPLIER", field = "Lcom/tacz/guns/config/sync/SyncConfig;DAMAGE_BASE_MULTIPLIER:Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;")
    @Definition(id = "get", method = "Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;get()Ljava/lang/Object;")
    @Definition(id = "Double", type = Double.class)
    @Expression("? * (Double) DAMAGE_BASE_MULTIPLIER.get()")
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double tacztweaks$getPropertyDiagramsData$damageModifier(double original) {
        return AttachmentPropertyManager.eval(Config.Modifiers.INSTANCE.damage(), original);
    }
}
