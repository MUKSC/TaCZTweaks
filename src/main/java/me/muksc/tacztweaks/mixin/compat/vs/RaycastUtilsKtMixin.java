package me.muksc.tacztweaks.mixin.compat.vs;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.muksc.tacztweaks.compat.vs.BlockHitResultExtension;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

@Mixin(value = RaycastUtilsKt.class, remap = false)
public abstract class RaycastUtilsKtMixin {
    @ModifyExpressionValue(method = "clipIncludeShips(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ClipContext;ZLjava/lang/Long;)Lnet/minecraft/world/phys/BlockHitResult;", at = @At(value = "INVOKE", target = "Lorg/valkyrienskies/mod/common/world/RaycastUtilsKt;clip(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ClipContext;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/BlockHitResult;"))
    private static BlockHitResult tacztweaks$clipIncludeShips$setShip(BlockHitResult original, @Local Ship ship) {
        BlockHitResultExtension ext = (BlockHitResultExtension) original;
        ext.tacztweaks$setShip(ship);
        return original;
    }
}
