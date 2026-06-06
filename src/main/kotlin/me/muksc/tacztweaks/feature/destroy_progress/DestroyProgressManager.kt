package me.muksc.tacztweaks.feature.destroy_progress

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level

object DestroyProgressManager {
    private val destroyProgress = Object2ObjectOpenHashMap<ResourceKey<Level>, Long2ObjectOpenHashMap<Progress>>()
    private const val TICKS_TO_RESET = 400

    fun addCurrentProgress(level: ServerLevel, pos: BlockPos, delta: Float): Boolean {
        val dimension = level.dimension()
        val longPos = pos.asLong()

        val map = destroyProgress.computeIfAbsent(dimension) { Long2ObjectOpenHashMap() }
        val progress = map.computeIfAbsent(longPos) { Progress() }.apply {
            this.delta += delta
            this.lastUpdated = level.gameTime
        }

        if (progress.delta >= 1.0F) {
            map.remove(longPos)
            level.destroyBlockProgress(getBreakerId(pos), pos, -1)
        } else if (progress.stage != progress.lastBroadcastStage) {
            level.destroyBlockProgress(getBreakerId(pos), pos, progress.stage)
            progress.lastBroadcastStage = progress.stage
        }
        return progress.delta >= 1.0F
    }

    fun onLevelTick(level: ServerLevel) {
        val map = destroyProgress[level.dimension()] ?: return
        val iterator = map.long2ObjectEntrySet().fastIterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val longPos = entry.longKey
            val progress = entry.value

            if (level.gameTime < (progress.lastUpdated + TICKS_TO_RESET)) continue
            if (progress.stage >= 0) {
                val pos = BlockPos.of(longPos)
                level.destroyBlockProgress(getBreakerId(pos), pos, -1)
            }
            iterator.remove()
        }
        if (map.isEmpty()) destroyProgress.remove(level.dimension())
    }

    fun onBlockBreak(level: ServerLevel, pos: BlockPos) {
        destroyProgress[level.dimension()]?.remove(pos.asLong())
        level.destroyBlockProgress(getBreakerId(pos), pos, -1)
    }

    private fun getBreakerId(pos: BlockPos) = pos.hashCode() or Int.MIN_VALUE

    private class Progress {
        var delta: Float = 0.0F
        var lastUpdated: Long = 0L

        val stage: Int get() = if (delta > 0.0F) (delta * 10.0F).toInt() else -1
        var lastBroadcastStage: Int = -1
    }
}