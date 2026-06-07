package me.muksc.tacztweaks.feature.raytracer

import com.tacz.guns.entity.EntityKineticBullet
import com.tacz.guns.entity.EntityKineticBullet.EntityResult
import com.tacz.guns.util.EntityUtil
import com.tacz.guns.util.TacHitResult
import me.muksc.tacztweaks.feature.datapack.legacy.manager.BulletInteractionManager
import me.muksc.tacztweaks.feature.datapack.legacy.manager.BulletParticlesManager
import me.muksc.tacztweaks.feature.datapack.legacy.manager.BulletSoundsManager
import me.muksc.tacztweaks.feature.general.compatibility.PillagersGunManager
import me.muksc.tacztweaks.mixin.accessor.EntityKineticBulletAccessor
import me.muksc.tacztweaks.mixininterface.feature.raytracer.RayTracingBullet
import me.muksc.tacztweaks.mixininterop.currentHitPosition
import me.muksc.tacztweaks.mixininterop.onHitBlock
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

object BulletHandler {
    fun handle(
        bullet: EntityKineticBullet,
        context: ClipContext,
        original: BlockHitResult,
        state: BlockState?
    ): BlockHitResult? {
        val accessor = bullet as EntityKineticBulletAccessor
        val ext = RayTracingBullet.of(bullet)

        val currentPosition = ext.currentHitPosition
        val entities = EntityUtil.findEntitiesOnPath(
            bullet, currentPosition, original.location
        )
        for (result in entities.sortedBy { currentPosition.distanceTo(it.hitPos) }) {
            if (PillagersGunManager.shouldIgnoreEntity(result.entity, bullet.owner)) continue
            ext.currentHitPosition = result.hitPos
            if (onHitEntity(result, bullet, context)) continue
            bullet.discard()
            return original
        }

        ext.currentHitPosition = original.location
        if (original.type == HitResult.Type.MISS || state == null) return original
        if (onHitBlock(original, state, bullet, context)) return null
        accessor.onHitBlock(original, context.from, context.to)
        return original
    }

    private fun onHitBlock(
        result: BlockHitResult,
        state: BlockState,
        bullet: EntityKineticBullet,
        context: ClipContext
    ): Boolean {
        val accessor = bullet as EntityKineticBulletAccessor
        val interactionResult = BulletInteractionManager.handleBlockInteraction(bullet, result, state)
        BulletParticlesManager.handleBlockParticle(interactionResult, bullet, result, state)
        BulletSoundsManager.handleBlockSound(interactionResult, bullet, result, state)
        if (!interactionResult.pierce) accessor.onHitBlock(result, context.from, context.to)
        return interactionResult.pierce
    }

    private fun onHitEntity(
        result: EntityResult,
        bullet: EntityKineticBullet,
        context: ClipContext
    ): Boolean {
        val interactionResult = BulletInteractionManager.handleEntityInteraction(bullet, TacHitResult(result), context)
        BulletParticlesManager.handleEntityParticle(interactionResult, bullet, result)
        BulletSoundsManager.handleEntitySound(interactionResult, bullet, result)
        return interactionResult.pierce
    }

    data class InteractionResult(
        val pierce: Boolean,
        val success: Boolean
    )
}