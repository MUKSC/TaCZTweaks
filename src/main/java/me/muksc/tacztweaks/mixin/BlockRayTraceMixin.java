package me.muksc.tacztweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.util.block.BlockRayTrace;
import me.muksc.tacztweaks.data.BulletInteractionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(value = BlockRayTrace.class, remap = false)
public abstract class BlockRayTraceMixin {
    @ModifyReturnValue(method = "lambda$rayTraceBlocks$1", at = @At("RETURN"))
    private static BlockHitResult onAmmoHitBlock(
        @Nullable BlockHitResult original,
        @Local(argsOnly = true) Level level,
        @Local(argsOnly = true) ClipContext context,
        @Local(argsOnly = true) BlockPos blockPos,
        @Nullable @Local BlockState blockState
    ) {
        if (original == null || original.getType() == HitResult.Type.MISS) return original;
        if (blockState == null || blockState.isAir()) return original;
        if (!(level instanceof ServerLevel serverLevel)) return original;
        return BulletInteractionManager.INSTANCE.handleInteraction(serverLevel, blockState, new BlockPos(blockPos)) ? null : original;
    }
}
