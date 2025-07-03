package me.muksc.tacztweaks.mixin.compat.vs;

import com.tacz.guns.util.block.BlockRayTrace;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.mixin.accessor.ClipContextAccessor;
import me.muksc.tacztweaks.mixininterface.compat.vs.ClipContextExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
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

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

@Mixin(value = BlockRayTrace.class, remap = false)
public abstract class BlockRayTraceMixin {
    @Unique
    private static BlockHitResult tacztweaks$result = null;

    @Unique
    private static Level tacztweaks$level = null;

    @Inject(method = "rayTraceBlocks", at = @At("HEAD"))
    private static void tacztweaks$rayTraceBlocks$storeLevel(Level level, ClipContext context, CallbackInfoReturnable<BlockHitResult> cir) {
        BlockRayTraceMixin.tacztweaks$level = level;
    }

    @Inject(method = "performRayTrace", at = @At("HEAD"), cancellable = true)
    private static <T> void tacztweaks$performRayTrace$vsCollisionCompat(ClipContext context, BiFunction<ClipContext, BlockPos, T> hitFunction, Function<ClipContext, T> missFactory, CallbackInfoReturnable<T> cir) {
        if (!Config.Compat.INSTANCE.vsCollisionCompat()) return;
        ClipContextAccessor accessor = (ClipContextAccessor) context;
        Entity entity = null;
        if (accessor.getCollisionContext() instanceof EntityCollisionContext entityCollisionContext) entity = entityCollisionContext.getEntity();

        ArrayList<BlockPos> ignores = new ArrayList<>();
        BlockHitResult result = tacztweaks$result = tacztweaks$level.clip(context);
        while (result.getType() != HitResult.Type.MISS) {
            ignores.add(result.getBlockPos());
            T t = hitFunction.apply(context, result.getBlockPos());
            if (t != null) {
                cir.setReturnValue(t);
                return;
            }

            ClipContext context2 = new ClipContext(result.getLocation(), context.getTo(), accessor.getBlock(), accessor.getFluid(), entity);
            ((ClipContextExtension) context2).tacztweaks$setIgnores(ignores);
            result = tacztweaks$result = tacztweaks$level.clip(context2);
        }
        cir.setReturnValue(missFactory.apply(context));
    }

    @Inject(method = "getBlockHitResult", at = @At("HEAD"), cancellable = true)
    private static void tacztweaks$getBlockHitResult$vsCollisionCompat(Level level, ClipContext rayTraceContext, BlockPos blockPos, BlockState blockState, CallbackInfoReturnable<BlockHitResult> cir) {
        if (!Config.Compat.INSTANCE.vsCollisionCompat()) return;
        cir.setReturnValue(tacztweaks$result);
    }
}
