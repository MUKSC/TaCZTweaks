package me.muksc.tacztweaks.mixin.feature.general.compatibility.vs;

//? if 1.20.1 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.config.common.AmmoConfig;
import com.tacz.guns.util.block.ProjectileExplosion;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.mixin.accessor.ExplosionAccessor;
import me.muksc.tacztweaks.mixininterface.feature.general.compatibility.vs.ExplosionInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Collections;
import java.util.List;

@Mixin(value = ProjectileExplosion.class, priority = 1500, remap = false)
public abstract class ProjectileExplosionMixin extends Explosion {
    public ProjectileExplosionMixin(Level level, @Nullable Entity source, double toBlowX, double toBlowY, double toBlowZ, float radius, List<BlockPos> positions) {
        super(level, source, toBlowX, toBlowY, toBlowZ, radius, positions);
    }

    @Shadow @Final private Level level;
    @Shadow @Final @Mutable private double x;
    @Shadow @Final @Mutable private double y;
    @Shadow @Final @Mutable private double z;
    @Shadow @Final private float radius;
    @Shadow @Final private boolean knockback;

    @Unique
    private boolean tacztweaks$isModifyingExplosion = false;

    @Inject(method = "explode", at = @At("TAIL"), remap = true)
    private void tacztweaks$explode$vsCompat(CallbackInfo ci) {
        if (!Config.General.Compatibility.vsCompat()) return;
        if (tacztweaks$isModifyingExplosion) {
            if (AmmoConfig.EXPLOSIVE_AMMO_KNOCK_BACK.get() && knockback) ExplosionInvoker.of(this).tacztweaks$invokeDoExplodeForce();
            return;
        }

        tacztweaks$isModifyingExplosion = true;
        double origX = this.x;
        double origY = this.y;
        double origZ = this.z;
        ExplosionAccessor accessor = (ExplosionAccessor) this;
        try {
            VSGameUtilsKt.transformToNearbyShipsAndWorld(this.level, this.x, this.y, this.z, this.radius, (x, y, z) -> {
                this.x = x;
                this.y = y;
                this.z = z;
                accessor.tacztweaks$setX(x);
                accessor.tacztweaks$setY(y);
                accessor.tacztweaks$setZ(z);
                explode();
            });
        } finally {
            this.x = origX;
            this.y = origY;
            this.z = origZ;
            tacztweaks$isModifyingExplosion = false;
        }
    }

    @WrapOperation(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"), remap = true)
    private List<?> tacztweaks$explode$vsCompat$skipRaytrace(Level instance, Entity entity, AABB aabb, Operation<List<?>> original) {
        if (!Config.General.Compatibility.vsCompat() || !tacztweaks$isModifyingExplosion) return original.call(instance, entity, aabb);
        return Collections.emptyList();
    }
}
//?} else {
/*import com.tacz.guns.util.block.ProjectileExplosion;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ProjectileExplosion.class)
public abstract class ProjectileExplosionMixin { }
*///?}