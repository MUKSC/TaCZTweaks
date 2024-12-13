package me.muksc.tacztweaks.data

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.registries.ForgeRegistries

sealed interface BulletInteractionBlock {
    fun test(state: BlockState): Boolean

    class InteractionBlock(val block: Block) : BulletInteractionBlock {
        override fun test(state: BlockState): Boolean = state.`is`(block)

        companion object {
            val CODEC = ForgeRegistries.BLOCKS.codec.xmap(::InteractionBlock) { it.block }
        }
    }

    class InteractionBlockTag(val blockTag: TagKey<Block>) : BulletInteractionBlock {
        override fun test(state: BlockState): Boolean = state.`is`(blockTag)

        companion object {
            val CODEC = TagKey.hashedCodec(Registries.BLOCK).xmap(::InteractionBlockTag) { it.blockTag }
        }
    }

    companion object {
        val CODEC = Codec.either(InteractionBlock.CODEC, InteractionBlockTag.CODEC)
            .xmap({ it.map({ it as BulletInteractionBlock }) { it as BulletInteractionBlock } }) { when (it) {
                is InteractionBlock -> Either.left(it)
                is InteractionBlockTag -> Either.right(it)
            } }
    }
}