package me.muksc.tacztweaks.feature.datapack.legacy.core

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Function

sealed interface BlockTestable {
    fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean

    companion object {
        val CODEC: Codec<BlockTestable> = Codec.either(BlockOrBlockTag.CODEC, BlockTarget.CODEC)
            .xmap({ it.map(Function.identity(), Function.identity()) }, { when (it) {
                is BlockOrBlockTag -> Either.left(it)
                is BlockTarget -> Either.right(it)
            } })
    }
}