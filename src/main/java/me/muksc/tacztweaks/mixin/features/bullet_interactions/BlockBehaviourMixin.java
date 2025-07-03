package me.muksc.tacztweaks.mixin.features.bullet_interactions;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.muksc.tacztweaks.mixininterface.features.bullet_interaction.DestroySpeedModifierHolder;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin implements DestroySpeedModifierHolder {
    @Unique
    private float tacztweaks$destroySpeedMultiplier = 1.0F;

    @Override
    public void tacztweaks$setDestroySpeedMultiplier(float multiplier) {
        tacztweaks$destroySpeedMultiplier = multiplier;
    }

    @ModifyExpressionValue(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"))
    private float tacztweaks$getDestroyProgress$applyDestroySpeedModifier(float original) {
        return original * tacztweaks$destroySpeedMultiplier;
    }
}
