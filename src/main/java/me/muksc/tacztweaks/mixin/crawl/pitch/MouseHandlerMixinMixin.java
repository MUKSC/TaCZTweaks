package me.muksc.tacztweaks.mixin.crawl.pitch;

import com.bawnorton.mixinsquared.TargetHandler;
import me.muksc.tacztweaks.config.Config;
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
    @ModifyConstant(method = "@MixinSquared:Handler", constant = @Constant(floatValue = 45.0F))
    private static float tacztweaks$getCrawlPitch$modifyPitchUpperLimit(float original) {
        return Config.Crawl.INSTANCE.pitchUpperLimit();
    }

    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.MouseHandlerMixin",
        name = "getCrawlPitch"
    )
    @ModifyConstant(method = "@MixinSquared:Handler", constant = @Constant(floatValue = -30.0F))
    private static float tacztweaks$getCrawlPitch$modifyPitchLowerLimit(float original) {
        if (!Config.Crawl.INSTANCE.dynamicPitchLimit()) return Config.Crawl.INSTANCE.pitchLowerLimit();

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return Config.Crawl.INSTANCE.pitchLowerLimit();
        BlockHitResult result = player.clientLevel.clip(new ClipContext(
            player.getEyePosition(),
            player.getEyePosition().add(player.getLookAngle().scale(1.5)),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        ));
        if (result.getType() == HitResult.Type.MISS) return Config.Crawl.INSTANCE.pitchLowerLimit();

        double distance = result.getLocation().distanceTo(player.getEyePosition());
        return (float) Math.max(Math.acos(distance), Config.Crawl.INSTANCE.pitchLowerLimit());
    }
}
