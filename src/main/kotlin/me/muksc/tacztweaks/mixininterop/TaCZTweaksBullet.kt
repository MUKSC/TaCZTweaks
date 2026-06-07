@file:Suppress("NOTHING_TO_INLINE")

package me.muksc.tacztweaks.mixininterop

import me.muksc.tacztweaks.mixininterface.feature.datapack.TaCZTweaksBullet
import net.minecraft.world.item.ItemStack

inline val TaCZTweaksBullet.gunStack: ItemStack
    get() = `tacztweaks$getGunStack`()

inline var TaCZTweaksBullet.burstIndex: Int
    get() = `tacztweaks$getBurstIndex`()
    set(value) = `tacztweaks$setBurstIndex`(value)

inline var TaCZTweaksBullet.pelletIndex: Int
    get() = `tacztweaks$getPelletIndex`()
    set(value) = `tacztweaks$setPelletIndex`(value)

inline var TaCZTweaksBullet.blockPierce: Int
    get() = `tacztweaks$getBlockPierce`()
    set(value) = `tacztweaks$setBlockPierce`(value)

inline var TaCZTweaksBullet.entityPierce: Int
    get() = `tacztweaks$getEntityPierce`()
    set(value) = `tacztweaks$setEntityPierce`(value)

inline fun TaCZTweaksBullet.modifyDamage(flat: Float, multiplier: Float) =
    `tacztweaks$modifyDamage`(flat, multiplier)

inline fun TaCZTweaksBullet.modifyEntityHitDamage(flat: Float, multiplier: Float) =
    `tacztweaks$modifyEntityHitDamage`(flat, multiplier)