package me.muksc.tacztweaks.feature.datapack.legacy.core

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import me.muksc.tacztweaks.core.codec.unwrapEither
import me.muksc.tacztweaks.core.registry.PlatformRegistries
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.Block as MCBlock

//? if forge
import me.muksc.tacztweaks.core.registry.PlatformRegistries.byNameCodec

sealed interface BlockOrBlockTag : BlockTestable {
    class Block(val block: MCBlock) : BlockOrBlockTag {
        override fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean = state.`is`(block)

        companion object {
            val CODEC: Codec<Block> = PlatformRegistries.BLOCK.byNameCodec().xmap(::Block, Block::block)
        }
    }

    class BlockTag(val tag: TagKey<MCBlock>) : BlockOrBlockTag {
        override fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean = state.`is`(tag)

        companion object {
            val CODEC: Codec<BlockTag> = TagKey.hashedCodec(Registries.BLOCK).xmap(::BlockTag, BlockTag::tag)
        }
    }

    companion object {
        val CODEC: Codec<BlockOrBlockTag> = Codec.either(Block.CODEC, BlockTag.CODEC)
            .xmap(::unwrapEither) { when (it) {
                is Block -> Either.left(it)
                is BlockTag -> Either.right(it)
            } }
    }
}