package me.muksc.tacztweaks.data

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.registries.ForgeRegistries

sealed interface BlockOrBlockTag {
    fun test(state: BlockState): Boolean

    class Block(val block: net.minecraft.world.level.block.Block) : BlockOrBlockTag {
        override fun test(state: BlockState): Boolean = state.`is`(block)

        companion object {
            val CODEC = ForgeRegistries.BLOCKS.codec.xmap(::Block, Block::block)
        }
    }

    class BlockTag(val tag: TagKey<net.minecraft.world.level.block.Block>) : BlockOrBlockTag {
        override fun test(state: BlockState): Boolean = state.`is`(tag)

        companion object {
            val CODEC = TagKey.hashedCodec(Registries.BLOCK).xmap(::BlockTag, BlockTag::tag)
        }
    }

    companion object {
        val CODEC = Codec.either(Block.CODEC, BlockTag.CODEC)
            .xmap({ it.map({ it as BlockOrBlockTag }) { it as BlockOrBlockTag } }) { when (it) {
                is Block -> Either.left(it)
                is BlockTag -> Either.right(it)
            } }
    }
}