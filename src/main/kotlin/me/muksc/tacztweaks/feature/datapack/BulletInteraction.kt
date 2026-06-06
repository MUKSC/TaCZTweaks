package me.muksc.tacztweaks.feature.datapack

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.codec.DispatchCodec
import me.muksc.tacztweaks.core.codec.DoublesMinMaxBoundsCodec
import me.muksc.tacztweaks.core.codec.LootItemConditionCodec
import me.muksc.tacztweaks.core.codec.strictOptionalFieldOf
import net.minecraft.advancements.critereon.MinMaxBounds
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition

sealed class BulletInteraction(
    val type: EBulletInteractionType,
    val conditions: List<LootItemCondition>,
    val pierce: Pierce,
    val priority: Int
) {
    fun test(context: LootContext): Boolean = conditions.all { it.test(context) }

    enum class EBulletInteractionType(
        override val key: String,
        override val codecProvider: () -> MapCodec<out BulletInteraction>
    ) : DispatchCodec<BulletInteraction> {
        BLOCK("block", { Block.CODEC }),
        ENTITY("entity", { Entity.CODEC });//,
        //SHIELD("shield", { Shield.CODEC });

        companion object {
            private val map = entries.associateBy(EBulletInteractionType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    sealed class Pierce(
        val type: EPierceType,
        val conditions: List<LootItemCondition>,
        val conditional: Boolean,
        val renderBulletHole: Boolean
    ) {
        enum class EPierceType(
            override val key: String,
            override val codecProvider: () -> MapCodec<out Pierce>
        ) : DispatchCodec<Pierce> {
            NEVER("never", { Never.CODEC }),
            ALWAYS("always", { Always.CODEC }),
            DEFAULT("default", { Default.CODEC }),
            COUNT("count", { Count.CODEC }),
            DAMAGE("damage", { Damage.CODEC });

            companion object {
                private val map = entries.associateBy(EPierceType::key)
                val CODEC = DispatchCodec.getCodec(map::getValue)
            }
        }

        object Never : Pierce(EPierceType.NEVER, emptyList(), false, false) {
            val CODEC: MapCodec<Never> = MapCodec.unit { Never }
        }

        class Always(
            conditions: List<LootItemCondition>,
            conditional: Boolean,
            renderBulletHole: Boolean
        ) : Pierce(EPierceType.ALWAYS, conditions, conditional, renderBulletHole) {
            companion object {
                val CODEC: MapCodec<Always> = RecordCodecBuilder.mapCodec { it.group(
                    Codec.list(LootItemConditionCodec).strictOptionalFieldOf("conditions", emptyList()).forGetter(Always::conditions),
                    Codec.BOOL.strictOptionalFieldOf("conditional", false).forGetter(Always::conditional),
                    Codec.BOOL.strictOptionalFieldOf("render_bullet_hole", false).forGetter(Always::renderBulletHole)
                ).apply(it, ::Always) }
            }
        }

        class Default(
            conditions: List<LootItemCondition>,
            conditional: Boolean,
            renderBulletHole: Boolean
        ) : Pierce(EPierceType.DEFAULT, conditions, conditional, renderBulletHole) {
            companion object {
                val CODEC: MapCodec<Default> = RecordCodecBuilder.mapCodec { it.group(
                    Codec.list(LootItemConditionCodec).strictOptionalFieldOf("conditions", emptyList()).forGetter(Default::conditions),
                    Codec.BOOL.strictOptionalFieldOf("conditional", false).forGetter(Default::conditional),
                    Codec.BOOL.strictOptionalFieldOf("render_bullet_hole", false).forGetter(Default::renderBulletHole)
                ).apply(it, ::Default) }
            }
        }

        class Count(
            conditions: List<LootItemCondition>,
            val count: Int,
            conditional: Boolean,
            renderBulletHole: Boolean
        ) : Pierce(EPierceType.COUNT, conditions, conditional, renderBulletHole) {
            companion object {
                val CODEC: MapCodec<Count> = RecordCodecBuilder.mapCodec { it.group(
                    Codec.list(LootItemConditionCodec).strictOptionalFieldOf("conditions", emptyList()).forGetter(Count::conditions),
                    Codec.INT.fieldOf("count").forGetter(Count::count),
                    Codec.BOOL.strictOptionalFieldOf("conditional", false).forGetter(Count::conditional),
                    Codec.BOOL.strictOptionalFieldOf("render_bullet_hole", false).forGetter(Count::renderBulletHole)
                ).apply(it, ::Count) }
            }
        }

        class Damage(
            conditions: List<LootItemCondition>,
            val damage: MinMaxBounds.Doubles,
            conditional: Boolean,
            renderBulletHole: Boolean
        ) : Pierce(EPierceType.DAMAGE, conditions, conditional, renderBulletHole) {
            companion object {
                val CODEC: MapCodec<Damage> = RecordCodecBuilder.mapCodec { it.group(
                    Codec.list(LootItemConditionCodec).strictOptionalFieldOf("conditions", emptyList()).forGetter(Damage::conditions),
                    DoublesMinMaxBoundsCodec.strictOptionalFieldOf("damage", MinMaxBounds.Doubles.atLeast(0.0)).forGetter(Damage::damage),
                    Codec.BOOL.strictOptionalFieldOf("conditional", false).forGetter(Damage::conditional),
                    Codec.BOOL.strictOptionalFieldOf("render_bullet_hole", false).forGetter(Damage::renderBulletHole)
                ).apply(it, ::Damage) }
            }
        }

        companion object {
            val CODEC: Codec<Pierce> = EPierceType.CODEC.dispatch(Pierce::type, EPierceType::codec)
        }
    }

    class Block(
        conditions: List<LootItemCondition>,
        val blockBreak: BlockBreak,
        pierce: Pierce,
        priority: Int
    ) : BulletInteraction(EBulletInteractionType.BLOCK, conditions, pierce, priority) {
        sealed class BlockBreak(
            val type: EBlockBreakType,
            val conditions: List<LootItemCondition>,
            val drop: Boolean
        ) {
            fun test(context: LootContext): Boolean = conditions.all { it.test(context) }

            enum class EBlockBreakType(
                override val key: String,
                override val codecProvider: () -> MapCodec<out BlockBreak>
            ) : DispatchCodec<BlockBreak> {
                NEVER("never", { Never.CODEC }),
                INSTANT("instant", { Instant.CODEC }),
                PERCENTAGE("percentage", { Percentage.CODEC }),
                FIXED_DAMAGE("fixed_damage", { FixedDamage.CODEC }),
                DYNAMIC_DAMAGE("dynamic_damage", { DynamicDamage.CODEC });

                companion object {
                    private val map = entries.associateBy(EBlockBreakType::key)
                    val CODEC = DispatchCodec.getCodec(map::getValue)
                }
            }

            class Never(
                drop: Boolean
            ) : BlockBreak(EBlockBreakType.NEVER, emptyList(), drop) {
                companion object {
                    val CODEC: MapCodec<Never> = RecordCodecBuilder.mapCodec { it.group(
                        Codec.BOOL.strictOptionalFieldOf("drop", false).forGetter(Never::drop)
                    ).apply(it, ::Never) }
                }
            }

            class Instant(
                conditions: List<LootItemCondition>,
                drop: Boolean
            ) : BlockBreak(EBlockBreakType.INSTANT, conditions, drop) {
                companion object {
                    val CODEC: MapCodec<Instant> = RecordCodecBuilder.mapCodec { it.group(
                        Codec.list(LootItemConditionCodec).strictOptionalFieldOf("conditions", emptyList()).forGetter(Instant::conditions),
                        Codec.BOOL.strictOptionalFieldOf("drop", false).forGetter(Instant::drop)
                    ).apply(it, ::Instant) }
                }
            }

            class Percentage(
                conditions: List<LootItemCondition>,
                val percentage: Float,
                drop: Boolean
            ) : BlockBreak(EBlockBreakType.PERCENTAGE, conditions, drop) {
                companion object {
                    val CODEC: MapCodec<Percentage> = RecordCodecBuilder.mapCodec { it.group(
                        Codec.list(LootItemConditionCodec).strictOptionalFieldOf("conditions", emptyList()).forGetter(Percentage::conditions),
                        Codec.FLOAT.fieldOf("percentage").forGetter(Percentage::percentage),
                        Codec.BOOL.strictOptionalFieldOf("drop", false).forGetter(Percentage::drop)
                    ).apply(it, ::Percentage) }
                }
            }

            class FixedDamage(
                conditions: List<LootItemCondition>,
                val damage: Float,
                val armorIgnore: Float,
                val accumulate: Boolean,
                drop: Boolean
            ) : BlockBreak(EBlockBreakType.FIXED_DAMAGE, conditions, drop) {
                companion object {
                    val CODEC: MapCodec<FixedDamage> = RecordCodecBuilder.mapCodec { it.group(
                        Codec.list(LootItemConditionCodec).strictOptionalFieldOf("conditions", emptyList()).forGetter(FixedDamage::conditions),
                        Codec.FLOAT.fieldOf("damage").forGetter(FixedDamage::damage),
                        Codec.FLOAT.strictOptionalFieldOf("armor_ignore", 0.0F).forGetter(FixedDamage::armorIgnore),
                        Codec.BOOL.strictOptionalFieldOf("accumulate", true).forGetter(FixedDamage::accumulate),
                        Codec.BOOL.strictOptionalFieldOf("drop", false).forGetter(FixedDamage::drop)
                    ).apply(it, ::FixedDamage) }
                }
            }

            class DynamicDamage(
                conditions: List<LootItemCondition>,
                val damageModifier: Float,
                val damageMultiplier: Float,
                val armorIgnoreModifier: Float,
                val armorIgnoreMultiplier: Float,
                val accumulate: Boolean,
                drop: Boolean
            ) : BlockBreak(EBlockBreakType.DYNAMIC_DAMAGE, conditions, drop) {
                companion object {
                    val CODEC: MapCodec<DynamicDamage> = RecordCodecBuilder.mapCodec { it.group(
                        Codec.list(LootItemConditionCodec).strictOptionalFieldOf("conditions", emptyList()).forGetter(DynamicDamage::conditions),
                        Codec.FLOAT.strictOptionalFieldOf("damage_modifier", 0.0F).forGetter(DynamicDamage::damageModifier),
                        Codec.FLOAT.strictOptionalFieldOf("damage_multiplier", 1.0F).forGetter(DynamicDamage::damageMultiplier),
                        Codec.FLOAT.strictOptionalFieldOf("armor_ignore_modifier", 0.0F).forGetter(DynamicDamage::armorIgnoreModifier),
                        Codec.FLOAT.strictOptionalFieldOf("armor_ignore_multiplier", 1.0F).forGetter(DynamicDamage::armorIgnoreMultiplier),
                        Codec.BOOL.strictOptionalFieldOf("accumulate", true).forGetter(DynamicDamage::accumulate),
                        Codec.BOOL.strictOptionalFieldOf("drop", false).forGetter(DynamicDamage::drop)
                    ).apply(it, ::DynamicDamage) }
                }
            }

            companion object {
                val CODEC: Codec<BlockBreak> = EBlockBreakType.CODEC.dispatch(BlockBreak::type, EBlockBreakType::codec)
            }
        }

        companion object {
            val CODEC: MapCodec<Block> = RecordCodecBuilder.mapCodec { it.group(
                Codec.list(LootItemConditionCodec).strictOptionalFieldOf("conditions", emptyList()).forGetter(Block::conditions),
                BlockBreak.CODEC.strictOptionalFieldOf("block_break", BlockBreak.Never(false)).forGetter(Block::blockBreak),
                Pierce.CODEC.strictOptionalFieldOf("pierce", Pierce.Never).forGetter(Block::pierce),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Block::priority)
            ).apply(it, ::Block) }
        }
    }

    class Entity(
        conditions: List<LootItemCondition>,
        val damage: Damage,
        pierce: Pierce,
        priority: Int
    ) : BulletInteraction(EBulletInteractionType.ENTITY, conditions, pierce, priority) {
        class Damage(
            val modifier: Float,
            val multiplier: Float
        ) {
            companion object {
                val CODEC: Codec<Damage> = RecordCodecBuilder.create { it.group(
                    Codec.FLOAT.strictOptionalFieldOf("modifier", 0.0F).forGetter(Damage::modifier),
                    Codec.FLOAT.strictOptionalFieldOf("multiplier", 1.0F).forGetter(Damage::multiplier)
                ).apply(it, ::Damage) }
            }
        }

        companion object {
            val CODEC: MapCodec<Entity> = RecordCodecBuilder.mapCodec { it.group(
                Codec.list(LootItemConditionCodec).strictOptionalFieldOf("conditions", emptyList()).forGetter(Entity::conditions),
                Damage.CODEC.strictOptionalFieldOf("damage", Damage(0.0F, 1.0F)).forGetter(Entity::damage),
                Pierce.CODEC.strictOptionalFieldOf("pierce", Pierce.Default(emptyList(), false, false)).forGetter(Entity::pierce),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Entity::priority)
            ).apply(it, ::Entity) }
        }
    }

    class Shield(
        conditions: List<LootItemCondition>,
    )

    companion object {
        val REGISTRY_KEY: ResourceKey<Registry<BulletInteraction>> = ResourceKey.createRegistryKey(
            TaCZTweaks.id("bullet_interactions")
        )
        val CODEC: Codec<BulletInteraction> = EBulletInteractionType.CODEC.dispatch(BulletInteraction::type, EBulletInteractionType::codec)
    }
}