package me.muksc.tacztweaks.feature.datapack.legacy

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.core.codec.*
import me.muksc.tacztweaks.feature.datapack.core.forGetter
import me.muksc.tacztweaks.feature.datapack.legacy.core.BlockTestable
import me.muksc.tacztweaks.feature.datapack.legacy.core.EntityTestable
import me.muksc.tacztweaks.feature.datapack.legacy.core.Target
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.commands.arguments.blocks.BlockInput
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

sealed class BulletInteraction(
    val type: EBulletInteractionType,
    val target: List<Target>,
    val priority: Int
) {
    enum class EBulletInteractionType(
        override val key: String,
        override val codecProvider: () -> MapCodec<out BulletInteraction>
    ) : DispatchCodec<BulletInteraction> {
        BLOCK("block", { Block.CODEC }),
        ENTITY("entity", { Entity.CODEC }),
        SHIELD("shield", { Shield.CODEC });

        companion object {
            private val map = EBulletInteractionType.entries.associateBy(EBulletInteractionType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    sealed class Pierce(
        val type: EPierceType,
        val conditional: Boolean,
        val damageFalloff: Float,
        val damageMultiplier: Float,
        val renderBulletHole: Boolean
    ) {
        enum class EPierceType(
            override val key: String,
            override val codecProvider: () -> MapCodec<out Pierce>
        ) : DispatchCodec<Pierce> {
            NEVER("never", { Never.CODEC }),
            DEFAULT("default", { Default.CODEC }),
            COUNT("count", { Count.CODEC }),
            DAMAGE("damage", { Damage.CODEC });

            companion object {
                private val map = EPierceType.entries.associateBy(EPierceType::key)
                val CODEC = DispatchCodec.getCodec(map::getValue)
            }
        }

        object Never : Pierce(EPierceType.NEVER, false, 0.0F, 1.0F, false) {
            val CODEC: MapCodec<Never> = MapCodec.unit(Never)
        }

        class Default(
            conditional: Boolean,
            damageFalloff: Float,
            damageMultiplier: Float,
            renderBulletHole: Boolean
        ) : Pierce(EPierceType.DEFAULT, conditional, damageFalloff, damageMultiplier, renderBulletHole) {
            companion object {
                val CODEC: MapCodec<Default> = RecordCodecBuilder.mapCodec { it.group(
                    Codec.BOOL.strictOptionalFieldOf("conditional", false).forGetter(Default::conditional),
                    Codec.FLOAT.strictOptionalFieldOf("damageFalloff", 0.0F).forGetter(Default::damageFalloff),
                    Codec.FLOAT.strictOptionalFieldOf("damageMultiplier", 1.0F).forGetter(Default::damageMultiplier),
                    Codec.BOOL.strictOptionalFieldOf("render_bullet_hole", false).forGetter(Default::renderBulletHole)
                ).apply(it, ::Default) }
            }
        }

        class Count(
            val count: Int,
            conditional: Boolean,
            damageFalloff: Float,
            damageMultiplier: Float,
            renderBulletHole: Boolean
        ) : Pierce(EPierceType.COUNT, conditional, damageFalloff, damageMultiplier, renderBulletHole) {
            companion object {
                val CODEC: MapCodec<Count> = RecordCodecBuilder.mapCodec { it.group(
                    Codec.INT.fieldOf("count").forGetter(Count::count),
                    Codec.BOOL.strictOptionalFieldOf("conditional", false).forGetter(Count::conditional),
                    Codec.FLOAT.strictOptionalFieldOf("damage_falloff", 0.0F).forGetter(Count::damageFalloff),
                    Codec.FLOAT.strictOptionalFieldOf("damage_multiplier", 1.0F).forGetter(Count::damageMultiplier),
                    Codec.BOOL.strictOptionalFieldOf("render_bullet_hole", false).forGetter(Count::renderBulletHole)
                ).apply(it, ::Count) }
            }
        }

        class Damage(
            conditional: Boolean,
            damageFalloff: Float,
            damageMultiplier: Float,
            renderBulletHole: Boolean
        ) : Pierce(EPierceType.DAMAGE, conditional, damageFalloff, damageMultiplier, renderBulletHole) {
            companion object {
                val CODEC: MapCodec<Damage> = RecordCodecBuilder.mapCodec { it.group(
                    Codec.BOOL.strictOptionalFieldOf("conditional", false).forGetter(Damage::conditional),
                    Codec.FLOAT.strictOptionalFieldOf("damage_falloff", 0.0F).forGetter(Damage::damageFalloff),
                    Codec.FLOAT.strictOptionalFieldOf("damage_multiplier", 1.0F).forGetter(Damage::damageMultiplier),
                    Codec.BOOL.strictOptionalFieldOf("render_bullet_hole", false).forGetter(Damage::renderBulletHole)
                ).apply(it, ::Damage) }
            }
        }

        companion object {
            val CODEC: Codec<Pierce> = EPierceType.CODEC.dispatch(Pierce::type) { it.codec() }
        }
    }

    class GunPierce(
        val required: Boolean,
        val consume: Boolean
    ) {
        companion object {
            fun codec(default: Boolean): Codec<GunPierce> =
                RecordCodecBuilder.create { it.group(
                    Codec.BOOL.strictOptionalFieldOf("required", default).forGetter(GunPierce::required),
                    Codec.BOOL.strictOptionalFieldOf("consume", default).forGetter(GunPierce::consume)
                ).apply(it, ::GunPierce) }
        }
    }

    class Block(
        target: List<Target>,
        val blocks: List<BlockTestable>,
        val blockBreak: BlockBreak,
        val pierce: Pierce,
        val gunPierce: GunPierce,
        priority: Int
    ) : BulletInteraction(EBulletInteractionType.BLOCK, target, priority) {
        sealed class BlockBreak(
            val type: EBlockBreakType,
            val replaceWith: BlockInput,
            val drop: Boolean
        ) {
            enum class EBlockBreakType(
                override val key: String,
                override val codecProvider: () -> MapCodec<out BlockBreak>
            ) : DispatchCodec<BlockBreak> {
                NEVER("never", { Never.CODEC }),
                INSTANT("instant", { Instant.CODEC }),
                COUNT("count", { Count.CODEC }),
                FIXED_DAMAGE("fixed_damage", { FixedDamage.CODEC }),
                DYNAMIC_DAMAGE("dynamic_damage", { DynamicDamage.CODEC });

                companion object {
                    private val map = entries.associateBy(EBlockBreakType::key)
                    val CODEC = DispatchCodec.getCodec(map::getValue)
                }
            }

            object Never : BlockBreak(EBlockBreakType.NEVER, Blocks.AIR.defaultBlockState().blockInput(), false) {
                val CODEC: MapCodec<Never> = MapCodec.unit(Never)
            }

            class Instant(
                replaceWith: BlockInput,
                drop: Boolean
            ) : BlockBreak(EBlockBreakType.INSTANT, replaceWith, drop) {
                companion object {
                    val CODEC: MapCodec<Instant> = RecordCodecBuilder.mapCodec { it.group(
                        BlockInputCodec.strictOptionalFieldOf("replace_with", Blocks.AIR.defaultBlockState().blockInput()).forGetter(Instant::replaceWith),
                        Codec.BOOL.strictOptionalFieldOf("drop", false).forGetter(Instant::drop)
                    ).apply(it, ::Instant) }
                }
            }

            class Count(
                val count: Int,
                replaceWith: BlockInput,
                drop: Boolean
            ) : BlockBreak(EBlockBreakType.COUNT, replaceWith, drop) {
                companion object {
                    val CODEC: MapCodec<Count> = RecordCodecBuilder.mapCodec { it.group(
                        Codec.INT.fieldOf("count").forGetter(Count::count),
                        BlockInputCodec.strictOptionalFieldOf("replace_with", Blocks.AIR.defaultBlockState().blockInput()).forGetter(Count::replaceWith),
                        Codec.BOOL.strictOptionalFieldOf("drop", false).forGetter(Count::drop)
                    ).apply(it, ::Count) }
                }
            }

            class FixedDamage(
                val damage: Float,
                val accumulate: Boolean,
                replaceWith: BlockInput,
                drop: Boolean
            ) : BlockBreak(EBlockBreakType.FIXED_DAMAGE, replaceWith, drop) {
                companion object {
                    val CODEC: MapCodec<FixedDamage> = RecordCodecBuilder.mapCodec { it.group(
                        Codec.FLOAT.fieldOf("damage").forGetter(FixedDamage::damage),
                        Codec.BOOL.strictOptionalFieldOf("accumulate", true).forGetter(FixedDamage::accumulate),
                        BlockInputCodec.strictOptionalFieldOf("replace_with", Blocks.AIR.defaultBlockState().blockInput()).forGetter(FixedDamage::replaceWith),
                        Codec.BOOL.strictOptionalFieldOf("drop", false).forGetter(FixedDamage::drop)
                    ).apply(it, ::FixedDamage) }
                }
            }

            class DynamicDamage(
                val modifier: Float,
                val multiplier: Float,
                val accumulate: Boolean,
                replaceWith: BlockInput,
                drop: Boolean
            ) : BlockBreak(EBlockBreakType.DYNAMIC_DAMAGE, replaceWith, drop) {
                companion object {
                    val CODEC: MapCodec<DynamicDamage> = RecordCodecBuilder.mapCodec { it.group(
                        Codec.FLOAT.strictOptionalFieldOf("modifier", 0.0F).forGetter(DynamicDamage::modifier),
                        Codec.FLOAT.strictOptionalFieldOf("multiplier", 1.0F).forGetter(DynamicDamage::multiplier),
                        Codec.BOOL.strictOptionalFieldOf("accumulate", true).forGetter(DynamicDamage::accumulate),
                        BlockInputCodec.strictOptionalFieldOf("replace_with", Blocks.AIR.defaultBlockState().blockInput()).forGetter(DynamicDamage::replaceWith),
                        Codec.BOOL.strictOptionalFieldOf("drop", false).forGetter(DynamicDamage::drop)
                    ).apply(it, ::DynamicDamage) }
                }
            }

            companion object {
                val CODEC: Codec<BlockBreak> = EBlockBreakType.CODEC.dispatch(BlockBreak::type) { it.codec() }
            }
        }

        companion object {
            val CODEC: MapCodec<Block> = RecordCodecBuilder.mapCodec { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Block::target),
                Codec.list(BlockTestable.CODEC).strictOptionalFieldOf("blocks", emptyList()).forGetter(Block::blocks),
                BlockBreak.CODEC.strictOptionalFieldOf("block_break", BlockBreak.Never).forGetter(Block::blockBreak),
                Pierce.CODEC.strictOptionalFieldOf("pierce", Pierce.Never).forGetter(Block::pierce),
                GunPierce.codec(false).strictOptionalFieldOf("gun_pierce", GunPierce(false, false)).forGetter(Block::gunPierce),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Block::priority)
            ).apply(it, ::Block) }
        }
    }

    class Entity(
        target: List<Target>,
        val entities: List<EntityTestable>,
        val damage: EntityDamage,
        val pierce: Pierce,
        val gunPierce: GunPierce,
        priority: Int
    ) : BulletInteraction(EBulletInteractionType.ENTITY, target, priority) {
        class EntityDamage(
            val modifier: Float,
            val multiplier: Float
        ) {
            companion object {
                val CODEC: Codec<EntityDamage> = RecordCodecBuilder.create { it.group(
                    Codec.FLOAT.strictOptionalFieldOf("modifier", 0.0F).forGetter(EntityDamage::modifier),
                    Codec.FLOAT.strictOptionalFieldOf("multiplier", 1.0F).forGetter(EntityDamage::multiplier)
                ).apply(it, ::EntityDamage) }
            }
        }

        companion object {
            val CODEC: MapCodec<Entity> = RecordCodecBuilder.mapCodec { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Entity::target),
                Codec.list(EntityTestable.CODEC).strictOptionalFieldOf("entities", emptyList()).forGetter(Entity::entities),
                EntityDamage.CODEC.strictOptionalFieldOf("damage", EntityDamage(0.0F, 1.0F)).forGetter(Entity::damage),
                Pierce.CODEC.strictOptionalFieldOf("pierce", Pierce.Default(false, 0.0F, 1.0F, false)).forGetter(Entity::pierce),
                GunPierce.codec(true).strictOptionalFieldOf("gun_pierce", GunPierce(true, true)).forGetter(Entity::gunPierce),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Entity::priority)
            ).apply(it, ::Entity) }
        }
    }

    class Shield(
        target: List<Target>,
        predicate: Optional<ItemPredicate>,
        val damage: ShieldDamage,
        val disable: Disable,
        val durability: Durability,
        priority: Int
    ) : BulletInteraction(EBulletInteractionType.SHIELD, target, priority) {
        val predicate: ItemPredicate? = predicate.getOrNull()

        class ShieldDamage(
            val falloff: Float,
            val multiplier: Float
        ) {
            companion object {
                val CODEC: Codec<ShieldDamage> = RecordCodecBuilder.create { it.group(
                    Codec.FLOAT.strictOptionalFieldOf("falloff", 0.0F).forGetter(ShieldDamage::falloff),
                    Codec.FLOAT.strictOptionalFieldOf("multiplier", 1.0F).forGetter(ShieldDamage::multiplier)
                ).apply(it, ::ShieldDamage) }
            }
        }

        class Disable(
            val duration: Int,
            val chance: Float,
            val conditional: Boolean
        ) {
            companion object {
                val CODEC: Codec<Disable> = RecordCodecBuilder.create { it.group(
                    Codec.INT.strictOptionalFieldOf("duration", 100).forGetter(Disable::duration),
                    Codec.FLOAT.strictOptionalFieldOf("chance", 1.0F).forGetter(Disable::chance),
                    Codec.BOOL.strictOptionalFieldOf("conditional", true).forGetter(Disable::conditional)
                ).apply(it, ::Disable) }
            }
        }

        sealed class Durability(
            val type: EDurabilityType,
            val conditional: Boolean
        ) {
            enum class EDurabilityType(
                override val key: String,
                override val codecProvider: () -> MapCodec<out Durability>
            ) : DispatchCodec<Durability> {
                FIXED_DAMAGE("fixed_damage", { FixedDamage.CODEC }),
                DYNAMIC_DAMAGE("dynamic_damage", { DynamicDamage.CODEC });

                companion object {
                    private val map = entries.associateBy(EDurabilityType::key)
                    val CODEC = DispatchCodec.getCodec(map::getValue)
                }
            }

            class FixedDamage(
                val damage: Float,
                conditional: Boolean
            ) : Durability(EDurabilityType.FIXED_DAMAGE, conditional) {
                companion object {
                    val CODEC: MapCodec<FixedDamage> = RecordCodecBuilder.mapCodec { it.group(
                        Codec.FLOAT.fieldOf("damage").forGetter(FixedDamage::damage),
                        Codec.BOOL.strictOptionalFieldOf("conditional", true).forGetter(FixedDamage::conditional)
                    ).apply(it, ::FixedDamage) }
                }
            }

            class DynamicDamage(
                val modifier: Float,
                val multiplier: Float,
                conditional: Boolean
            ) : Durability(EDurabilityType.DYNAMIC_DAMAGE, conditional) {
                companion object {
                    val CODEC: MapCodec<DynamicDamage> = RecordCodecBuilder.mapCodec { it.group(
                        Codec.FLOAT.strictOptionalFieldOf("modifier", 0.0F).forGetter(DynamicDamage::modifier),
                        Codec.FLOAT.strictOptionalFieldOf("multiplier", 1.0F).forGetter(DynamicDamage::multiplier),
                        Codec.BOOL.strictOptionalFieldOf("conditional", true).forGetter(DynamicDamage::conditional)
                    ).apply(it, ::DynamicDamage) }
                }
            }

            companion object {
                val CODEC: Codec<Durability> = EDurabilityType.CODEC.dispatch(Durability::type) { it.codec() }
            }
        }

        companion object {
            val CODEC: MapCodec<Shield> = RecordCodecBuilder.mapCodec { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Shield::target),
                ItemPredicateCodec.strictOptionalFieldOf("predicate").forGetter(Shield::predicate),
                ShieldDamage.CODEC.strictOptionalFieldOf("damage", ShieldDamage(0.0F, 1.0F)).forGetter(Shield::damage),
                Disable.CODEC.strictOptionalFieldOf("disable", Disable(0, 0.0F, true)).forGetter(Shield::disable),
                Durability.CODEC.strictOptionalFieldOf("durability", Durability.FixedDamage(0.0F, true)).forGetter(Shield::durability),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Shield::priority)
            ).apply(it, ::Shield) }
        }
    }

    companion object {
        // Load order stuff
        //init { Target; Pierce; Block.BlockBreak }
        val CODEC: Codec<BulletInteraction> = EBulletInteractionType.CODEC.dispatch(BulletInteraction::type) { it.codec() }

        fun BlockState.blockInput(): BlockInput = BlockInput(this, emptySet(), null)
    }
}