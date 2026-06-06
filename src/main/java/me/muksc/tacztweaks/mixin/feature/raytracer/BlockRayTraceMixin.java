package me.muksc.tacztweaks.mixin.feature.raytracer;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tacz.guns.util.block.BlockRayTrace;
import me.muksc.tacztweaks.feature.raytracer.BulletRayTracer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.BiFunction;
import java.util.function.Function;

@Mixin(value = BlockRayTrace.class, remap = false)
public abstract class BlockRayTraceMixin {
    @WrapMethod(method = "rayTraceBlocks")
    private static BlockHitResult tacztweaks$rayTraceBlocks$rayTracer(Level level, ClipContext context, Operation<BlockHitResult> original) {
        return BulletRayTracer.rayTraceBlocks(
            new BulletRayTracer.RayTraceBlocksArgs(level, context),
            (args) -> original.call(args.level, args.context)
        );
    }

    @WrapMethod(method = "getBlockHitResult")
    private static BlockHitResult tacztweaks$getBlockHitResult$rayTracer(Level level, ClipContext rayTraceContext, BlockPos blockPos, BlockState blockState, Operation<BlockHitResult> original) {
        return BulletRayTracer.getBlockHitResult(
            new BulletRayTracer.GetBlockHitResultArgs(level, rayTraceContext, blockPos, blockState),
            (args) -> original.call(args.level, args.context, args.pos, args.state)
        );
    }

    @WrapMethod(method = "performRayTrace")
    private static <T> T tacztweaks$performRayTrace$rayTracer(ClipContext context, BiFunction<ClipContext, BlockPos, T> hitFunction, Function<ClipContext, T> missFactory, Operation<T> original) {
        return BulletRayTracer.performRayTrace(
            new BulletRayTracer.PerformRayTraceArgs<>(context, hitFunction, missFactory),
            (args) -> original.call(args.context, args.hitFunction, args.missFactory)
        );
    }
}