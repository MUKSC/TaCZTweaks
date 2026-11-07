package me.muksc.tacztweaks.mixin.feature.gameplay.crawl;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.feature.gameplay.crawl.DynamicPitchLimit;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MouseHandler.class, priority = 1500, remap = false)
public abstract class MouseHandlerMixinMixin {
    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.MouseHandlerMixin",
        name = "getCrawlPitch"
    )
    //~ config_spec
    @Definition(id = "CRAWL_MAX_UP_ANGLE", field = "Lcom/tacz/guns/config/sync/SyncConfig;CRAWL_MAX_UP_ANGLE:Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;")
    //~ config_spec
    @Definition(id = "get", method = "Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;get()Ljava/lang/Object;")
    @Definition(id = "Double", type = Double.class)
    @Expression("-(Double) CRAWL_MAX_UP_ANGLE.get()")
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static double tacztweaks$getCrawlPitch$dynamicPitchLimit(double original) {
        if (!Config.Gameplay.Crawl.dynamicPitchLimit()) return original;
        Double dynamicPitchLimit = DynamicPitchLimit.getDynamicPitchLimitOrNull();
        if (dynamicPitchLimit == null) return original;
        return Math.max(dynamicPitchLimit, original);
    }
}