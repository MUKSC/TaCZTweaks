package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.data.codec.DispatchCodec
import me.muksc.tacztweaks.data.BulletInteraction.Block.BlockBreak
import me.muksc.tacztweaks.data.codec.singleOrListCodec
import me.muksc.tacztweaks.data.codec.strictOptionalFieldOf
import me.muksc.tacztweaks.data.core.BlockTestable
import me.muksc.tacztweaks.data.core.Target

sealed class MeleeInteraction(
    val type: EMeleeInteractionType,
    val target: List<Target>,
    val priority: Int
) {
    enum class EMeleeInteractionType(
        override val key: String,
        override val codecProvider: () -> Codec<out MeleeInteraction>
    ) : DispatchCodec<MeleeInteraction> {
        BLOCK("block", { Block.CODEC });

        companion object {
            private val map = EMeleeInteractionType.entries.associateBy(EMeleeInteractionType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    class Block(
        target: List<Target>,
        val blocks: List<BlockTestable>,
        val blockBreak: BlockBreak,
        priority: Int
    ) : MeleeInteraction(EMeleeInteractionType.BLOCK, target, priority) {
        companion object {
            val CODEC: Codec<Block> = RecordCodecBuilder.create<Block> { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Block::target),
                Codec.list(BlockTestable.CODEC).strictOptionalFieldOf("blocks", emptyList()).forGetter(Block::blocks),
                BlockBreak.CODEC.strictOptionalFieldOf("block_break", BlockBreak.Never).forGetter(Block::blockBreak),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Block::priority)
            ).apply(it, ::Block) }
        }
    }

    companion object {
        val CODEC: Codec<MeleeInteraction> = EMeleeInteractionType.CODEC.dispatch(MeleeInteraction::type) { it.codecProvider() }
    }
}