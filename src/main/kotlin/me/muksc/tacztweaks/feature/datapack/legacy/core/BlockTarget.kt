package me.muksc.tacztweaks.feature.datapack.legacy.core

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.core.codec.BlockPredicateCodec
import me.muksc.tacztweaks.core.codec.DispatchCodec
import me.muksc.tacztweaks.core.codec.TierSortingRegistryCodec
import me.muksc.tacztweaks.core.extension.id
import me.muksc.tacztweaks.core.registry.PlatformRegistries
import net.minecraft.advancements.critereon.BlockPredicate
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Tier
import net.minecraft.world.level.block.Block as MCBlock
import net.minecraft.world.level.block.state.BlockState

//? if forge {
import me.muksc.tacztweaks.core.registry.PlatformRegistries.byNameCodec
import net.minecraftforge.common.TierSortingRegistry
//?}

sealed class BlockTarget(
    val type: EBlockTargetType
) : BlockTestable {
    enum class EBlockTargetType(
        override val key: String,
        override val codecProvider: () -> MapCodec<out BlockTarget>
    ) : DispatchCodec<BlockTarget> {
        ALL_OF("all_of", { AllOf.CODEC }),
        ANY_OF("any_of", { AnyOf.CODEC }),
        INVERTED("inverted", { Inverted.CODEC }),
        BLOCK("block", { Block.CODEC }),
        BLOCK_TAG("block_tag", { BlockTag.CODEC }),
        REGEX("regex", { RegexPattern.CODEC }),
        PREDICATE("predicate", { Predicate.CODEC }),
        TIER("tier", { HardnessTier.CODEC }),
        HARDNESS("hardness", { Hardness.CODEC });

        companion object {
            private val map = entries.associateBy(EBlockTargetType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    class AllOf(val terms: List<BlockTarget>) : BlockTarget(EBlockTargetType.ALL_OF) {
        override fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean =
            terms.all { it.test(level, pos, state) }

        companion object {
            val CODEC: MapCodec<AllOf> = RecordCodecBuilder.mapCodec { it.group(
                Codec.list(BlockTarget.CODEC).fieldOf("terms").forGetter(AllOf::terms)
            ).apply(it, ::AllOf) }
        }
    }

    class AnyOf(val terms: List<BlockTarget>) : BlockTarget(EBlockTargetType.ANY_OF) {
        override fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean =
            terms.any { it.test(level, pos, state) }

        companion object {
            val CODEC: MapCodec<AnyOf> = RecordCodecBuilder.mapCodec { it.group(
                Codec.list(BlockTarget.CODEC).fieldOf("terms").forGetter(AnyOf::terms)
            ).apply(it, ::AnyOf) }
        }
    }

    class Inverted(val term: BlockTarget) : BlockTarget(EBlockTargetType.INVERTED) {
        override fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean = !term.test(level, pos, state)

        companion object {
            val CODEC: MapCodec<Inverted> = RecordCodecBuilder.mapCodec { it.group(
                BlockTarget.CODEC.fieldOf("term").forGetter(Inverted::term)
            ).apply(it, ::Inverted) }
        }
    }

    class Block(val values: List<MCBlock>) : BlockTarget(EBlockTargetType.BLOCK) {
        override fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean =
            values.any { state.`is`(it) }

        companion object {
            val CODEC: MapCodec<Block> = RecordCodecBuilder.mapCodec { it.group(
                Codec.list(PlatformRegistries.BLOCK.byNameCodec()).fieldOf("values").forGetter(Block::values)
            ).apply(it, ::Block) }
        }
    }

    class BlockTag(val values: List<TagKey<MCBlock>>) : BlockTarget(EBlockTargetType.BLOCK_TAG) {
        override fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean =
            values.any { state.`is`(it) }

        companion object {
            val CODEC: MapCodec<BlockTag> = RecordCodecBuilder.mapCodec { it.group(
                Codec.list(TagKey.hashedCodec(Registries.BLOCK)).fieldOf("values").forGetter(BlockTag::values)
            ).apply(it, ::BlockTag) }
        }
    }

    class RegexPattern(val regex: Regex) : BlockTarget(EBlockTargetType.REGEX) {
        override fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean =
            regex.matches(state.block.id.toString())

        companion object {
            val CODEC: MapCodec<RegexPattern> = RecordCodecBuilder.mapCodec { it.group(
                Codec.STRING.xmap(::Regex, Regex::pattern).fieldOf("regex").forGetter(RegexPattern::regex)
            ).apply(it, ::RegexPattern) }
        }
    }

    class Predicate(val predicate: BlockPredicate) : BlockTarget(EBlockTargetType.PREDICATE) {
        override fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean =
            predicate.matches(level, pos)

        companion object {
            val CODEC: MapCodec<Predicate> = RecordCodecBuilder.mapCodec { it.group(
                BlockPredicateCodec.fieldOf("predicate").forGetter(Predicate::predicate)
            ).apply(it, ::Predicate) }
        }
    }

    class HardnessTier(val tier: Tier) : BlockTarget(EBlockTargetType.TIER) {
        override fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean =
            //? if 1.21.1 {
            /*!state.`is`(tier.incorrectBlocksForDrops)
            *///?} else if fabric {
            /*tier.level >= net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager.getRequiredMiningLevel(state)
            *///?} else if forge {
            TierSortingRegistry.isCorrectTierForDrops(tier, state)
            //?}

        companion object {
            val CODEC: MapCodec<HardnessTier> = RecordCodecBuilder.mapCodec { it.group(
                TierSortingRegistryCodec.fieldOf("tier").forGetter(HardnessTier::tier)
            ).apply(it, ::HardnessTier) }
        }
    }

    class Hardness(val range: ValueRange) : BlockTarget(EBlockTargetType.HARDNESS) {
        override fun test(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean =
            state.getDestroySpeed(level, pos) in range

        companion object {
            val CODEC: MapCodec<Hardness> = RecordCodecBuilder.mapCodec { it.group(
                ValueRange.CODEC.fieldOf("range").forGetter(Hardness::range)
            ).apply(it, ::Hardness) }
        }
    }

    companion object {
        val CODEC: Codec<BlockTarget> = EBlockTargetType.CODEC.dispatch(BlockTarget::type) { it.codec() }
    }
}