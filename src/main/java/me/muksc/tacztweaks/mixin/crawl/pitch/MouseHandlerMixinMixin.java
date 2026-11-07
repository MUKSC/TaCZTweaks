package me.muksc.tacztweaks.mixin.crawl.pitch;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MouseHandler.class, priority = 1500, remap = false)
public abstract class MouseHandlerMixinMixin {
    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.MouseHandlerMixin",
        name = "getCrawlPitch"
    )
    @Definition(id = "CRAWL_MAX_UP_ANGLE", field = "Lcom/tacz/guns/config/sync/SyncConfig;CRAWL_MAX_UP_ANGLE:Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;")
    @Definition(id = "get", method = "Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;get()Ljava/lang/Object;")
    @Definition(id = "Double", type = Double.class)
    @Expression("-(Double) CRAWL_MAX_UP_ANGLE.get()")
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static double tacztweaks$getCrawlPitch$dynamicPitchLimit(double original) {
        if (!Config.Crawl.INSTANCE.dynamicPitchLimit()) return original;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return original;
        BlockHitResult result = player.clientLevel.clip(new ClipContext(
            player.getEyePosition(),
            player.getEyePosition().add(player.getLookAngle().scale(1.5)),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        ));
        if (result.getType() == HitResult.Type.MISS) return original;

        double distance = result.getLocation().distanceTo(player.getEyePosition());
        return Math.max(Math.acos(distance), original);
    }
}