package me.muksc.tacztweaks.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import me.muksc.tacztweaks.Config;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = MouseHandler.class, priority = 1500)
public abstract class MouseHandlerMixinMixin {
    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.MouseHandlerMixin",
        name = "getCrawlPitch"
    )
    @ModifyConstant(method = "@MixinSquared:Handler", constant = @Constant(floatValue = 25.0F))
    private static float modifyPitchUpperLimit(float original) {
        return Config.crawlPitchUpperLimit;
    }

    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.MouseHandlerMixin",
        name = "getCrawlPitch"
    )
    @ModifyConstant(method = "@MixinSquared:Handler", constant = @Constant(floatValue = -10.0F))
    private static float modifyPitchLowerLimit(float original) {
        return Config.crawlPitchLowerLimit;
    }
}
