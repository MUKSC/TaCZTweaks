package me.muksc.tacztweaks.feature.raytracer

import com.tacz.guns.entity.EntityKineticBullet
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.mixin.accessor.ClipContextAccessor
import me.muksc.tacztweaks.mixininterface.feature.raytracer.IgnoringClipContext
import me.muksc.tacztweaks.mixininterface.feature.raytracer.RayTracingBullet
import me.muksc.tacztweaks.mixininterop.*
import net.minecraft.core.BlockPos
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.shapes.EntityCollisionContext
import java.util.function.BiFunction
import java.util.function.Function

object BulletRayTracer {
    private var level: Level? = null
    private var result: BlockHitResult? = null

    @JvmStatic
    fun rayTraceBlocks(
        args: RayTraceBlocksArgs,
        operation: (RayTraceBlocksArgs) -> BlockHitResult
    ): BlockHitResult = try {
        level = null
        run {
            val bullet = getBullet(args.context) ?: return@run
            val ext = RayTracingBullet.of(bullet)
            ext.currentHitPosition = args.context.from
            level = args.level
        }
        operation(args)
    } finally {
        level = null
        result = null
    }

    @JvmStatic
    fun <T> performRayTrace(
        args: PerformRayTraceArgs<T>,
        operation: (PerformRayTraceArgs<T>) -> T
    ): T {
        val modifiedArgs = args.copy(hitFunction = { context, pos ->
            val original = args.hitFunction.apply(context, pos)
            if (original !is BlockHitResult) return@copy original
            val state = level?.getBlockState(pos) ?: return@copy original

            @Suppress("UNCHECKED_CAST")
            handle(context, original, state) as T?
        }, missFactory = { context ->
            val original = args.missFactory.apply(context)
            if (original !is BlockHitResult) return@copy original

            @Suppress("UNCHECKED_CAST")
            handle(context, original, null) as T? ?: return@copy original
        })
        return when {
            Config.General.Compatibility.vsCompat()
                || Config.General.Compatibility.sableCompat() -> performCompatibilityRayTrace(modifiedArgs, operation)
            else -> operation(modifiedArgs)
        }
    }

    @JvmStatic
    fun getBlockHitResult(
        args: GetBlockHitResultArgs,
        operation: (GetBlockHitResultArgs) -> BlockHitResult?
    ): BlockHitResult? = result ?: operation(args)

    private fun <T> performCompatibilityRayTrace(
        args: PerformRayTraceArgs<T>,
        operation: (PerformRayTraceArgs<T>) -> T
    ): T {
        val level = this.level ?: return operation(args)
        val accessor = args.context as ClipContextAccessor
        val entity = (accessor.collisionContext as? EntityCollisionContext)?.entity ?: return operation(args)

        val ignores = arrayListOf<BlockPos>()
        var result = level.clip(args.context).also(this::result::set)
        while (result.type != HitResult.Type.MISS) {
            ignores.add(result.blockPos)
            val value = args.hitFunction.apply(args.context, result.blockPos)
            if (value != null) return value

            val newContext = ClipContext(
                result.location, args.context.to,
                accessor.block, accessor.fluid, entity
            ).apply { IgnoringClipContext.of(this).`tacztweaks$setIgnores`(ignores) }
            result = level.clip(newContext).also(this::result::set)
        }

        return args.missFactory.apply(args.context)
    }

    private fun getBullet(context: ClipContext): EntityKineticBullet? {
        val accessor = context as ClipContextAccessor
        val collisionContext = accessor.collisionContext as? EntityCollisionContext ?: return null
        return collisionContext.entity as? EntityKineticBullet
    }

    private fun handle(context: ClipContext, original: BlockHitResult, state: BlockState?): BlockHitResult? {
        return BulletHandler.handle(getBullet(context) ?: return null, context, original, state)
    }

    data class RayTraceBlocksArgs(
        @JvmField val level: Level,
        @JvmField val context: ClipContext
    )

    data class GetBlockHitResultArgs(
        @JvmField val level: Level,
        @JvmField val context: ClipContext,
        @JvmField val pos: BlockPos,
        @JvmField val state: BlockState
    )

    data class PerformRayTraceArgs<T>(
        @JvmField val context: ClipContext,
        @JvmField val hitFunction: BiFunction<ClipContext, BlockPos, T?>,
        @JvmField val missFactory: Function<ClipContext, T>
    )
}