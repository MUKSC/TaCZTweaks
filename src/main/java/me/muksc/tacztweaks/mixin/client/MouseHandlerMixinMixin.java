package me.muksc.tacztweaks.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import me.muksc.tacztweaks.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = MouseHandler.class, priority = 1500, remap = false)
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
        if (!Config.dynamicCrawlPitchLimit) return Config.crawlPitchLowerLimit;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return Config.crawlPitchLowerLimit;
        BlockHitResult result = player.clientLevel.clip(new ClipContext(
            player.getEyePosition(),
            player.getEyePosition().add(player.getLookAngle().scale(1.5)),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        ));
        if (result.getType() == HitResult.Type.MISS) return Config.crawlPitchLowerLimit;

        double distance = result.getLocation().distanceTo(player.getEyePosition());
        return (float) Math.max(Math.acos(distance), Config.crawlPitchLowerLimit);
    }
}
