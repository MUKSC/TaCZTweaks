package me.muksc.tacztweaks.mixin.compat.vs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.entity.EntityKineticBullet;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.mixininterface.compat.vs.BlockHitResultWithShip;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin {
    @SuppressWarnings("MixinExtrasOperationParameters")
    @WrapOperation(method = "onHitBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", ordinal = 0, remap = true))
    private <T extends ParticleOptions> int tacztweaks$onHitBlock$transformPosition(
        ServerLevel instance, T pType,
        double pPosX, double pPosY, double pPosZ,
        int pParticleCount,
        double pXOffset, double pYOffset, double pZOffset,
        double pSpeed,
        Operation<Integer> original,
        @Local(argsOnly = true) BlockHitResult result
    ) {
        if (!Config.Compat.INSTANCE.vsCollisionCompat()) return original.call(instance, pType, pPosX, pPosY, pPosZ, pParticleCount, pXOffset, pYOffset, pZOffset, pSpeed);
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(instance, result.getBlockPos());

        Vector3d pos = new Vector3d(pPosX, pPosY, pPosZ);
        if (ship != null) pos = ship.getTransform().getWorldToShip().transformPosition(pos);
        return original.call(instance, pType, pos.x, pos.y, pos.z, pParticleCount, pXOffset, pYOffset, pZOffset, pSpeed);
    }
}
