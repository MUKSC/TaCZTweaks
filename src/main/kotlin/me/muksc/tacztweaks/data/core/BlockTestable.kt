package me.muksc.tacztweaks.data.core

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import me.muksc.tacztweaks.identity
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState

sealed interface BlockTestable {
    fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean

    companion object {
        val CODEC: Codec<BlockTestable> = Codec.either(BlockOrBlockTag.CODEC, BlockTarget.CODEC)
            .xmap({ it.map(::identity, ::identity) }, { when (it) {
                is BlockOrBlockTag -> Either.left(it)
                is BlockTarget -> Either.right(it)
            } })
    }
}