package me.muksc.tacztweaks.mixin.feature.general.compatibility.sable;

//? if 1.21.1 {
/*import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.EntityUtil;
import dev.ryanhcode.sable.Sable;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(value = EntityUtil.class, remap = false)
public abstract class EntityUtilMixin {
    @WrapMethod(method = "findEntityOnPath")
    private static EntityKineticBullet.EntityResult tacztweaks$findEntityOnPath$sableCompat(Projectile bulletEntity, Vec3 startVec, Vec3 endVec, Operation<EntityKineticBullet.EntityResult> original) {
        if (!Config.General.Compatibility.sableCompat()) return original.call(bulletEntity, startVec, endVec);
        return original.call(
            bulletEntity,
            Sable.HELPER.projectOutOfSubLevel(bulletEntity.level(), startVec),
            Sable.HELPER.projectOutOfSubLevel(bulletEntity.level(), endVec)
        );
    }

    @WrapMethod(method = "findEntitiesOnPath")
    private static @NotNull List<EntityKineticBullet.EntityResult> tacztweaks$findEntitiesOnPath$sableCompat(Projectile bulletEntity, Vec3 startVec, Vec3 endVec, Operation<List<EntityKineticBullet.EntityResult>> original) {
        if (!Config.General.Compatibility.sableCompat()) return original.call(bulletEntity, startVec, endVec);
        return original.call(
            bulletEntity,
            Sable.HELPER.projectOutOfSubLevel(bulletEntity.level(), startVec),
            Sable.HELPER.projectOutOfSubLevel(bulletEntity.level(), endVec)
        );
    }
}
*///?} else {
import com.tacz.guns.util.EntityUtil;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityUtil.class)
public abstract class EntityUtilMixin { }
//?}