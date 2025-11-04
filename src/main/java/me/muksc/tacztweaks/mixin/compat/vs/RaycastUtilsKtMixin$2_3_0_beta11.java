package me.muksc.tacztweaks.mixin.compat.vs;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.mixininterface.compat.vs.BlockHitResultWithShip;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

@Mixin(value = RaycastUtilsKt.class, remap = false)
public abstract class RaycastUtilsKtMixin$2_3_0_beta11 {
    @ModifyExpressionValue(method = "clipIncludeShips(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ClipContext;ZLjava/lang/Long;Z)Lnet/minecraft/world/phys/BlockHitResult;", at = @At(value = "INVOKE", target = "Lorg/valkyrienskies/mod/common/world/RaycastUtilsKt;clip(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ClipContext;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/BlockHitResult;"))
    private static BlockHitResult tacztweaks$clipIncludeShips$setShip(BlockHitResult original, @Local LoadedShip ship) {
        if (!Config.Compat.INSTANCE.vsCollisionCompat()) return original;
        BlockHitResultWithShip ext = (BlockHitResultWithShip) original;
        ext.tacztweaks$setShip(ship);
        return original;
    }
}
