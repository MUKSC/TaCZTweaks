package me.muksc.tacztweaks.mixin.feature.datapack.bullet_interactions;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.muksc.tacztweaks.mixininterface.feature.datapack.bullet_interactions.DestroySpeedModifiableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin implements DestroySpeedModifiableBlock {
    @Unique
    private float tacztweaks$destroySpeedMultiplier = 1.0F;

    @Override
    public void tacztweaks$setDestroySpeedMultiplier(float multiplier) {
        tacztweaks$destroySpeedMultiplier = multiplier;
    }

    @ModifyExpressionValue(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"))
    private float tacztweaks$getDestroyProgress$modifyDestroySpeed(float original) {
        return original * tacztweaks$destroySpeedMultiplier;
    }
}