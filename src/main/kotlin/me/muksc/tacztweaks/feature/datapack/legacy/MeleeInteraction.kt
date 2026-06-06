package me.muksc.tacztweaks.feature.datapack.legacy

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.core.codec.DispatchCodec
import me.muksc.tacztweaks.core.codec.singleOrListCodec
import me.muksc.tacztweaks.core.codec.strictOptionalFieldOf
import me.muksc.tacztweaks.feature.datapack.legacy.core.BlockTestable
import me.muksc.tacztweaks.feature.datapack.legacy.core.Target
import me.muksc.tacztweaks.feature.datapack.legacy.BulletInteraction.Block.BlockBreak

sealed class MeleeInteraction(
    val type: EMeleeInteractionType,
    val target: List<Target>,
    val priority: Int
) {
    enum class EMeleeInteractionType(
        override val key: String,
        override val codecProvider: () -> MapCodec<out MeleeInteraction>
    ) : DispatchCodec<MeleeInteraction> {
        BLOCK("block", { Block.CODEC });

        companion object {
            private val map = entries.associateBy(EMeleeInteractionType::key)
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
            val CODEC: MapCodec<Block> = RecordCodecBuilder.mapCodec { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Block::target),
                Codec.list(BlockTestable.CODEC).strictOptionalFieldOf("blocks", emptyList()).forGetter(Block::blocks),
                BlockBreak.CODEC.strictOptionalFieldOf("block_break", BlockBreak.Never).forGetter(Block::blockBreak),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Block::priority)
            ).apply(it, ::Block) }
        }
    }

    companion object {
        val CODEC: Codec<MeleeInteraction> = EMeleeInteractionType.CODEC.dispatch(MeleeInteraction::type, EMeleeInteractionType::codec)
    }
}