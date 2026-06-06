package me.muksc.tacztweaks.mixin.feature.gameplay.crawl;

import com.bawnorton.mixinsquared.TargetHandler;
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
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "CONSTANT", args = "floatValue=45.0F"))
    private static float tacztweaks$getCrawlPitch$pitchUpperLimit(float original) {
        return Config.Gameplay.Crawl.pitchUpperLimit();
    }

    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.MouseHandlerMixin",
        name = "getCrawlPitch"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "CONSTANT", args = "floatValue=-30.0F"))
    private static float tacztweaks$getCrawlPitch$pitchLowerLimit(float original) {
        if (!Config.Gameplay.Crawl.dynamicPitchLimit()) return Config.Gameplay.Crawl.pitchLowerLimit();
        Double dynamicPitchLimit = DynamicPitchLimit.getDynamicPitchLimitOrNull();
        if (dynamicPitchLimit == null) return Config.Gameplay.Crawl.pitchLowerLimit();
        return (float) Math.max(dynamicPitchLimit, Config.Gameplay.Crawl.pitchLowerLimit());
    }
}