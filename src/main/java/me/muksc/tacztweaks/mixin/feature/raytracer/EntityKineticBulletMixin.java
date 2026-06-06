package me.muksc.tacztweaks.mixin.feature.raytracer;

import com.tacz.guns.entity.EntityKineticBullet;
import me.muksc.tacztweaks.mixininterface.feature.raytracer.RayTracingBullet;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin implements RayTracingBullet {
    @Inject(method = "onBulletTick", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/block/BlockRayTrace;rayTraceBlocks(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;", shift = At.Shift.AFTER), cancellable = true)
    private void tacztweaks$onBulletTick$finishRayTracing(CallbackInfo ci) {
        ci.cancel();
    }

    @Unique
    private Vec3 tacztweaks$currentHitPosition = Vec3.ZERO;

    @Override
    public Vec3 tacztweaks$getCurrentHitPosition() {
        return tacztweaks$currentHitPosition;
    }

    @Override
    public void tacztweaks$setCurrentHitPosition(Vec3 position) {
        tacztweaks$currentHitPosition = position;
    }
}