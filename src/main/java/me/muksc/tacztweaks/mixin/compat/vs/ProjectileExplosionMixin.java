package me.muksc.tacztweaks.mixin.compat.vs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.config.common.AmmoConfig;
import com.tacz.guns.util.block.ProjectileExplosion;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.compat.vs.ExplosionInvoker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Collections;
import java.util.List;

@Mixin(value = ProjectileExplosion.class, remap = false)
public abstract class ProjectileExplosionMixin {
    @Shadow @Final private Level level;
    @Shadow @Final @Mutable private double x;
    @Shadow @Final @Mutable private double y;
    @Shadow @Final @Mutable private double z;
    @Shadow @Final private float radius;
    @Shadow @Final private boolean knockback;

    @Shadow(remap = true) public abstract void explode();

    @Unique
    private boolean tacztweaks$isModifyingExplosion = false;

    @Inject(method = "explode", at = @At("TAIL"), remap = true)
    private void afterExplode(CallbackInfo ci) {
        if (!Config.vsExplosionCompat) return;
        var invoker = (ExplosionInvoker) this;
        if (tacztweaks$isModifyingExplosion) {
            if (AmmoConfig.EXPLOSIVE_AMMO_KNOCK_BACK.get() && knockback) invoker.tacztweaks$invokeDoExplodeForce();
            return;
        }

        tacztweaks$isModifyingExplosion = true;
        double origX = this.x;
        double origY = this.y;
        double origZ = this.z;
        try {
            VSGameUtilsKt.transformToNearbyShipsAndWorld(this.level, this.x, this.y, this.z, this.radius, (x, y, z) -> {
                this.x = x;
                this.y = y;
                this.z = z;
                this.explode();
            });
        } finally {
            this.x = origX;
            this.y = origY;
            this.z = origZ;
            tacztweaks$isModifyingExplosion = false;
        }
    }

    @WrapOperation(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"), remap = true)
    private List<Entity> noRayTrace(Level instance, Entity entity, AABB aabb, Operation<List<Entity>> original) {
        if (!Config.vsExplosionCompat || !tacztweaks$isModifyingExplosion) original.call(instance, entity, aabb);
        return Collections.emptyList();
    }
}
