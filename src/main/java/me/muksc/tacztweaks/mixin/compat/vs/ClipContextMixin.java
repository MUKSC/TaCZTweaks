package me.muksc.tacztweaks.mixin.compat.vs;

import me.muksc.tacztweaks.compat.vs.ClipContextExtension;
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
public abstract class ClipContextMixin implements ClipContextExtension {
    @Unique
    private Set<BlockPos> tacztweaks$ignores = new HashSet<>();

    @Override
    public void tacztweaks$setIgnores(Collection<BlockPos> ignores) {
        tacztweaks$ignores.addAll(ignores);
    }

    @Inject(method = "getBlockShape", at = @At("HEAD"), cancellable = true)
    private void ignoreBlock(BlockState pBlockState, BlockGetter pLevel, BlockPos pPos, CallbackInfoReturnable<VoxelShape> cir) {
        if (!tacztweaks$ignores.contains(pPos)) return;
        cir.setReturnValue(Shapes.empty());
    }

    @Inject(method = "getFluidShape", at = @At("HEAD"), cancellable = true)
    private void ignoreFluid(FluidState pState, BlockGetter pLevel, BlockPos pPos, CallbackInfoReturnable<VoxelShape> cir) {
        if (!tacztweaks$ignores.contains(pPos)) return;
        cir.setReturnValue(Shapes.empty());
    }
}
