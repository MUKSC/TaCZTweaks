package me.muksc.tacztweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.block.BlockRayTrace;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.FlatDamageModifierHolder;
import me.muksc.tacztweaks.TaCZTweaks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(value = BlockRayTrace.class, remap = false)
public abstract class BlockRayTraceMixin {
    @ModifyExpressionValue(method = "lambda$rayTraceBlocks$1", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private static boolean onAmmoHitBlock(
        boolean original,
        @Local(argsOnly = true) Level level,
        @Local(argsOnly = true) ClipContext context,
        @Local(argsOnly = true) BlockPos blockPos,
        @Nullable @Local BlockState blockState
    ) {
        if (original) return true;
        if (blockState == null || !blockState.is(TaCZTweaks.BULLET_BREAK_BLOCKS) && !blockState.is(TaCZTweaks.BULLET_BREAK_WITH_DROP_BLOCKS)) return false;

        EntityKineticBullet ammo = TaCZTweaks.ammoInstance;
        level.destroyBlock(blockPos, blockState.is(TaCZTweaks.BULLET_BREAK_WITH_DROP_BLOCKS), ammo.getOwner());
        if (!Config.pierceBlocks) return false;

        FlatDamageModifierHolder holder = (FlatDamageModifierHolder) ammo;
        holder.tacztweaks$setFlatDamageModifier(holder.tacztweaks$getFlatDamageModifier() - Config.pierceDamageFalloff);
        return ammo.getDamage(blockPos.getCenter()) > 0.0F;
    }
}
