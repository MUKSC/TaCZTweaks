package me.muksc.tacztweaks.mixininterop

import me.muksc.tacztweaks.mixin.accessor.ClipContextAccessor
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.shapes.CollisionContext

inline val ClipContextAccessor.block: ClipContext.Block
    get() = `tacztweaks$getBlock`()

inline val ClipContextAccessor.fluid: ClipContext.Fluid
    get() = `tacztweaks$getFluid`()

inline val ClipContextAccessor.collisionContext: CollisionContext
    get() = `tacztweaks$getCollisionContext`()