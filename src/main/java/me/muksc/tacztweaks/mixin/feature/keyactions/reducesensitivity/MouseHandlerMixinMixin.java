package me.muksc.tacztweaks.mixin.feature.keyactions.reducesensitivity;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.muksc.tacztweaks.client.input.ReduceSensitivityKey;
import me.muksc.tacztweaks.client.input.TiltGunKey;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MouseHandler.class, priority = 1500, remap = false)
public abstract class MouseHandlerMixinMixin {
    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.MouseHandlerMixin",
        name = "reduceSensitivity"
    )
    @Expression("1.0 + (? - 1.0) * (double) ?")
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private double tacztweaks$reduceSensitivity$reduceSensitivityKey(
        double original,
        @Local(argsOnly = true) LocalPlayer player
    ) {
        boolean tiltGunReduceSensitivity = Config.KeyActions.TiltGun.reduceSensitivity() && TiltGunKey.isActive(player);
        if (!ReduceSensitivityKey.KEY.isDown() && !tiltGunReduceSensitivity) return original;
        return original * ReduceSensitivityKey.getCurrentSensitivityMultiplier(player);
    }
}