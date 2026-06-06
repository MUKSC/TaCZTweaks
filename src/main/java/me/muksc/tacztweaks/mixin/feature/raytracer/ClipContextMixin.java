package me.muksc.tacztweaks.mixin.feature.raytracer;

import me.muksc.tacztweaks.mixininterface.feature.raytracer.IgnoringClipContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Mixin(ClipContext.class)
public abstract class ClipContextMixin implements IgnoringClipContext {
    @Unique
    private final Set<BlockPos> tacztweaks$ignores = new HashSet<>();

    @Override
    public void tacztweaks$setIgnores(Collection<BlockPos> ignores) {
        tacztweaks$ignores.addAll(ignores);
    }

    @Inject(method = "getBlockShape", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$getBlockShape$ignore(BlockState blockState, BlockGetter level, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (!tacztweaks$ignores.contains(pos)) return;
        cir.setReturnValue(Shapes.empty());
    }

    @Inject(method = "getFluidShape", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$getFluidShape$ignore(FluidState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (!tacztweaks$ignores.contains(pos)) return;
        cir.setReturnValue(Shapes.empty());
    }
}