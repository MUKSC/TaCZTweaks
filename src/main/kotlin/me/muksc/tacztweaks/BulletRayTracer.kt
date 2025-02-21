package me.muksc.tacztweaks

import com.tacz.guns.entity.EntityKineticBullet
import com.tacz.guns.util.EntityUtil
import com.tacz.guns.util.TacHitResult
import me.muksc.tacztweaks.data.BulletInteractionManager
import me.muksc.tacztweaks.data.BulletSoundsManager
import me.muksc.tacztweaks.data.BulletSoundsManager.EBlockSoundType
import me.muksc.tacztweaks.data.BulletSoundsManager.EEntitySoundType
import me.muksc.tacztweaks.mixin.accessor.EntityKineticBulletAccessor
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class BulletRayTracer(
    val entity: EntityKineticBullet,
    val level: ServerLevel,
    val context: ClipContext
) {
    private val accessor = entity as EntityKineticBulletAccessor
    private val ext = entity as EntityKineticBulletExtension
    private var findEntitiesStart = context.from

    fun handle(original: BlockHitResult, state: BlockState?): BlockHitResult? {
        val entities = EntityUtil.findEntitiesOnPath(entity, findEntitiesStart, original.location)
        for (result in entities.sortedBy { findEntitiesStart.distanceTo(it.hitPos) }) {
            ext.`tacztweaks$setPosition`(result.hitPos)
            val interactionResult = BulletInteractionManager.handleEntityInteraction(TacHitResult(result), context)
            BulletSoundsManager.handleEntitySound(interactionResult.toEntitySoundType(), level, entity, result.hitPos, result.entity)
            if (interactionResult.pierce) continue
            entity.discard()
            return original
        }
        ext.`tacztweaks$setPosition`(original.location)
        findEntitiesStart = original.location

        if (original.type == HitResult.Type.MISS || state == null) return original
        val interactionResult = BulletInteractionManager.handleBlockInteraction(original, state)
        BulletSoundsManager.handleBlockSound(interactionResult.toBlockSoundType(), level, entity, original.location, state)

        if (interactionResult.pierce) return null
        accessor.invokeOnHitBlock(original, context.from, context.to)
        return original
    }

    private fun BulletInteractionManager.InteractionResult.toBlockSoundType(): EBlockSoundType = when {
        condition -> EBlockSoundType.BREAK
        pierce -> EBlockSoundType.PIERCE
        else -> EBlockSoundType.HIT
    }

    private fun BulletInteractionManager.InteractionResult.toEntitySoundType(): EEntitySoundType = when {
        condition -> EEntitySoundType.KILL
        pierce -> EEntitySoundType.PIERCE
        else -> EEntitySoundType.HIT
    }
}