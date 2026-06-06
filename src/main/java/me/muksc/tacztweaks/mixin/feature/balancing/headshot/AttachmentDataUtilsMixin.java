package me.muksc.tacztweaks.mixin.feature.balancing.headshot;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.util.AttachmentDataUtils;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AttachmentDataUtils.class, remap = false)
public abstract class AttachmentDataUtilsMixin {
    //~ config_spec
    @Definition(id = "HEAD_SHOT_BASE_MULTIPLIER", field = "Lcom/tacz/guns/config/sync/SyncConfig;HEAD_SHOT_BASE_MULTIPLIER:Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;")
    //~ config_spec
    @Definition(id = "get", method = "Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;get()Ljava/lang/Object;")
    @Definition(id = "Double", type = Double.class)
    @Expression("? * (Double) HEAD_SHOT_BASE_MULTIPLIER.get()")
    @ModifyExpressionValue(method = "getHeadshotMultiplier", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static double tacztweaks$getHeadshotMultiplier$headshotModifier(double original) {
        return Config.Balancing.Headshot.eval(original);
    }
}