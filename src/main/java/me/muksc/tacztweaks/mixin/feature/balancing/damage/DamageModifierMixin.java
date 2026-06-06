package me.muksc.tacztweaks.mixin.feature.balancing.damage;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.resource.modifier.custom.DamageModifier;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = DamageModifier.class, remap = false)
public abstract class DamageModifierMixin {
    //~ config_spec
    @Definition(id = "DAMAGE_BASE_MULTIPLIER", field = "Lcom/tacz/guns/config/sync/SyncConfig;DAMAGE_BASE_MULTIPLIER:Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;")
    //~ config_spec
    @Definition(id = "get", method = "Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;get()Ljava/lang/Object;")
    @Definition(id = "Double", type = Double.class)
    @Expression("? * (Double) DAMAGE_BASE_MULTIPLIER.get()")
    @ModifyExpressionValue(method = "initCache", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double tacztweaks$initCache$damageModifier(double original) {
        return Config.Balancing.Damage.eval(original);
    }

    //~ environment environment_client
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    //~ config_spec
    @Definition(id = "DAMAGE_BASE_MULTIPLIER", field = "Lcom/tacz/guns/config/sync/SyncConfig;DAMAGE_BASE_MULTIPLIER:Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;")
    //~ config_spec
    @Definition(id = "get", method = "Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;get()Ljava/lang/Object;")
    @Definition(id = "Double", type = Double.class)
    @Expression("? * (Double) DAMAGE_BASE_MULTIPLIER.get()")
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double tacztweaks$getPropertyDiagramsData$damageModifier(double original) {
        return Config.Balancing.Damage.eval(original);
    }
}