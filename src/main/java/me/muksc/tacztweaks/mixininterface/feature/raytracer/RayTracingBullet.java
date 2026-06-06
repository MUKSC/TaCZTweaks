package me.muksc.tacztweaks.mixininterface.feature.raytracer;

import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.world.phys.Vec3;

public interface RayTracingBullet {
    static RayTracingBullet of(EntityKineticBullet instance) {
        return (RayTracingBullet) instance;
    }

    Vec3 tacztweaks$getCurrentHitPosition();

    void tacztweaks$setCurrentHitPosition(Vec3 position);
}