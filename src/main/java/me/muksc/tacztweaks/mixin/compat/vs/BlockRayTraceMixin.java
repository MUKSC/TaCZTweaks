package me.muksc.tacztweaks.mixin.compat.vs;

import com.tacz.guns.config.common.AmmoConfig;
import com.tacz.guns.init.ModBlocks;
import com.tacz.guns.util.block.BlockRayTrace;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.compat.vs.ClipContextExtension;
import me.muksc.tacztweaks.data.BulletInteractionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = BlockRayTrace.class, remap = false)
public abstract class BlockRayTraceMixin {
    @Unique
    private static boolean tacztweaks$shouldCollide(Level level, ClipContext context, BlockHitResult result) {
        BlockPos blockPos = result.getBlockPos();
        BlockState state = level.getBlockState(blockPos);
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        List<String> passThrough = AmmoConfig.PASS_THROUGH_BLOCKS.get();
        if ((id != null && passThrough.contains(id.toString())) || state.is(ModBlocks.BULLET_IGNORE_BLOCKS)) {
            return false;
        }
        if (level instanceof ServerLevel serverLevel) {
            double distance = context.getFrom().distanceTo(result.getLocation());
            Vec3 worldPos = VSGameUtilsKt.toWorldCoordinates(level, result.getLocation());
            return !BulletInteractionManager.INSTANCE.handleInteraction(serverLevel, state, distance, blockPos, worldPos);
        }
        return true;
    }

    @Inject(method = "rayTraceBlocks", at = @At("HEAD"), cancellable = true)
    private static void compatibilityClip(Level level, ClipContext context, CallbackInfoReturnable<BlockHitResult> cir) {
        if (!Config.vsCollisionCompat) return;
        ClipContextAccessor accessor = (ClipContextAccessor) context;
        Entity entity = null;
        if (accessor.getCollisionContext() instanceof EntityCollisionContext entityCollisionContext) entity = entityCollisionContext.getEntity();

        ArrayList<BlockPos> ignores = new ArrayList<>();
        BlockHitResult result = level.clip(context);
        ignores.add(result.getBlockPos());
        while (result.getType() != HitResult.Type.MISS) {
            if (tacztweaks$shouldCollide(level, context, result)) break;
            ClipContext context2 = new ClipContext(result.getLocation(), context.getTo(), accessor.getBlock(), accessor.getFluid(), entity);
            ((ClipContextExtension) context2).tacztweaks$setIgnores(ignores);
            result = level.clip(context2);
            ignores.add(result.getBlockPos());
        }
        cir.setReturnValue(result);
    }
}
