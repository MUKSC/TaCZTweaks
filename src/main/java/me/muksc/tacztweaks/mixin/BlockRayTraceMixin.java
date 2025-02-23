package me.muksc.tacztweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.block.BlockRayTrace;
import me.muksc.tacztweaks.BulletRayTracer;
import me.muksc.tacztweaks.mixin.accessor.ClipContextAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = BlockRayTrace.class, remap = false)
public abstract class BlockRayTraceMixin {
    @Unique
    private static BulletRayTracer tacztweaks$rayTracer;

    @Inject(method = "rayTraceBlocks", at = @At("HEAD"))
    private static void initRayTracer(Level level, ClipContext context, CallbackInfoReturnable<BlockHitResult> cir) {
        tacztweaks$rayTracer = null;
        if (!(level instanceof ServerLevel serverLevel)) return;
        ClipContextAccessor accessor = (ClipContextAccessor) context;
        if (!(accessor.getCollisionContext() instanceof EntityCollisionContext entityCollisionContext)) return;
        if (!(entityCollisionContext.getEntity() instanceof EntityKineticBullet entity)) return;
        tacztweaks$rayTracer = new BulletRayTracer(entity, serverLevel, context);
    }

    @ModifyReturnValue(method = "lambda$rayTraceBlocks$1", at = @At("RETURN"))
    private static BlockHitResult handle(
        @Nullable BlockHitResult original,
        @Local(argsOnly = true) ClipContext context,
        @Local(argsOnly = true) BlockPos blockPos,
        @Nullable @Local BlockState blockState
    ) {
        if (tacztweaks$rayTracer == null) return original;
        if (original == null || original.getType() == HitResult.Type.MISS) return original;
        if (blockState == null || blockState.isAir()) return original;
        return tacztweaks$rayTracer.handle(tacztweaks$rayTracer.getEntity(), original, blockState);
    }

    @ModifyReturnValue(method = "lambda$rayTraceBlocks$2", at = @At("RETURN"))
    private static BlockHitResult handleMiss(BlockHitResult original) {
        if (tacztweaks$rayTracer == null) return original;
        return tacztweaks$rayTracer.handle(tacztweaks$rayTracer.getEntity(), original, null);
    }
}
