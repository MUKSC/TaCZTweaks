package me.muksc.tacztweaks.mixininterop

import me.muksc.tacztweaks.mixininterface.feature.raytracer.RayTracingBullet
import net.minecraft.world.phys.Vec3

inline var RayTracingBullet.currentHitPosition: Vec3
    get() = `tacztweaks$getCurrentHitPosition`()
    set(value) = `tacztweaks$setCurrentHitPosition`(value)