package me.muksc.tacztweaks.data.core

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.registries.ForgeRegistries

sealed interface BlockOrBlockTag : BlockTestable {
    class Block(val block: net.minecraft.world.level.block.Block) : BlockOrBlockTag {
        override fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean = state.`is`(block)

        companion object {
            val CODEC: Codec<Block> = ForgeRegistries.BLOCKS.codec.xmap(::Block, Block::block)
        }
    }

    class BlockTag(val tag: TagKey<net.minecraft.world.level.block.Block>) : BlockOrBlockTag {
        override fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean = state.`is`(tag)

        companion object {
            val CODEC: Codec<BlockTag> = TagKey.hashedCodec(Registries.BLOCK).xmap(::BlockTag, BlockTag::tag)
        }
    }

    companion object {
        val CODEC: Codec<BlockOrBlockTag> = Codec.either(Block.CODEC, BlockTag.CODEC)
            .xmap({ value -> value.map({ it as BlockOrBlockTag }) { it as BlockOrBlockTag } }) { when (it) {
                is Block -> Either.left(it)
                is BlockTag -> Either.right(it)
            } }
    }
}