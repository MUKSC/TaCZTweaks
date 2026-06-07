@file:Suppress("NOTHING_TO_INLINE")

package me.muksc.tacztweaks.mixininterop

import com.tacz.guns.util.TacHitResult
import me.muksc.tacztweaks.mixin.accessor.EntityKineticBulletAccessor
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

inline var EntityKineticBulletAccessor.pierce: Int
    get() = `tacztweaks$getPierce`()
    set(value) = `tacztweaks$setPierce`(value)

inline val EntityKineticBulletAccessor.explosion: Boolean
    get() = `tacztweaks$getExplosion`()

inline fun EntityKineticBulletAccessor.onHitEntity(result: TacHitResult, startVec: Vec3, endVec: Vec3) =
    `tacztweaks$invokeOnHitEntity`(result, startVec, endVec)

inline fun EntityKineticBulletAccessor.onHitBlock(result: BlockHitResult, startVec: Vec3, endVec: Vec3) =
    `tacztweaks$invokeOnHitBlock`(result, startVec, endVec)