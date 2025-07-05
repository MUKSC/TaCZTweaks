package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.DispatchCodec
import me.muksc.tacztweaks.data.BulletInteraction.Block.BlockBreak
import me.muksc.tacztweaks.singleOrListCodec
import me.muksc.tacztweaks.strictOptionalFieldOf
import net.minecraft.world.item.Tier
import java.util.*
import kotlin.jvm.optionals.getOrNull

sealed class BulletInteraction(
    val type: EBulletInteractionType,
    val target: List<Target>,
    val pierce: Pierce,
    val gunPierce: GunPierce
) {
    enum class EBulletInteractionType(
        override val key: String,
        override val codecProvider: () -> Codec<out BulletInteraction>
    ) : DispatchCodec<BulletInteraction> {
        BLOCK("block", { Block.CODEC }),
        ENTITY("entity", { Entity.CODEC });

        companion object {
            private val map = EBulletInteractionType.entries.associateBy(EBulletInteractionType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    sealed class Pierce(
        val type: EPierceType,
        val conditional: Boolean,
        val damageFalloff: Float,
        val damageMultiplier: Float
    ) {
        enum class EPierceType(
            override val key: String,
            override val codecProvider: () -> Codec<out Pierce>
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

        object Never : Pierce(EPierceType.NEVER, false, 0.0F, 1.0F) {
            val CODEC = Codec.unit(Never)
        }

        class Default(
            conditional: Boolean,
            damageFalloff: Float,
            damageMultiplier: Float
        ) : Pierce(EPierceType.DEFAULT, conditional, damageFalloff, damageMultiplier) {
            companion object {
                val CODEC = RecordCodecBuilder.create<Default> { it.group(
                    Codec.BOOL.strictOptionalFieldOf("conditional", false).forGetter(Default::conditional),
                    Codec.FLOAT.strictOptionalFieldOf("damageFalloff", 0.0F).forGetter(Default::damageFalloff),
                    Codec.FLOAT.strictOptionalFieldOf("damageMultiplier", 1.0F).forGetter(Default::damageMultiplier)
                ).apply(it, ::Default) }
            }
        }

        class Count(
            val count: Int,
            conditional: Boolean,
            damageFalloff: Float,
            damageMultiplier: Float
        ) : Pierce(EPierceType.COUNT, conditional, damageFalloff, damageMultiplier) {
            companion object {
                val CODEC = RecordCodecBuilder.create<Count> { it.group(
                    Codec.INT.fieldOf("count").forGetter(Count::count),
                    Codec.BOOL.strictOptionalFieldOf("conditional", false).forGetter(Count::conditional),
                    Codec.FLOAT.strictOptionalFieldOf("damage_falloff", 0.0F).forGetter(Count::damageFalloff),
                    Codec.FLOAT.strictOptionalFieldOf("damage_multiplier", 1.0F).forGetter(Count::damageMultiplier)
                ).apply(it, ::Count) }
            }
        }

        class Damage(
            conditional: Boolean,
            damageFalloff: Float,
            damageMultiplier: Float
        ) : Pierce(EPierceType.DAMAGE, conditional, damageFalloff, damageMultiplier) {
            companion object {
                val CODEC = RecordCodecBuilder.create<Damage> { it.group(
                    Codec.BOOL.strictOptionalFieldOf("conditional", false).forGetter(Damage::conditional),
                    Codec.FLOAT.strictOptionalFieldOf("damage_falloff", 0.0F).forGetter(Damage::damageFalloff),
                    Codec.FLOAT.strictOptionalFieldOf("damage_multiplier", 1.0F).forGetter(Damage::damageMultiplier)
                ).apply(it, ::Damage) }
            }
        }

        companion object {
            val CODEC = EPierceType.CODEC.dispatch(Pierce::type) { it.codecProvider() }
        }
    }

    class GunPierce(
        val required: Boolean,
        val consume: Boolean
    ) {
        companion object {
            fun codec(default: Boolean): Codec<GunPierce> =
                RecordCodecBuilder.create<GunPierce> { it.group(
                    Codec.BOOL.strictOptionalFieldOf("required", default).forGetter(GunPierce::required),
                    Codec.BOOL.strictOptionalFieldOf("consume", default).forGetter(GunPierce::consume)
                ).apply(it, ::GunPierce) }
        }
    }

    class Block(
        target: List<Target>,
        val blocks: List<BlockTestable>,
        val blockBreak: BlockBreak,
        pierce: Pierce,
        gunPierce: GunPierce
    ) : BulletInteraction(EBulletInteractionType.BLOCK, target, pierce, gunPierce) {
        sealed class BlockBreak(
            val type: EBlockBreakType,
            val hardness: ValueRange,
            val tier: Tier?,
            val drop: Boolean
        ) {
            enum class EBlockBreakType(
                override val key: String,
                override val codecProvider: () -> Codec<out BlockBreak>
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

            object Never : BlockBreak(EBlockBreakType.NEVER, ValueRange.DEFAULT, null, false) {
                val CODEC = Codec.unit(Never)
            }

            class Instant(
                hardness: ValueRange,
                tier: Optional<Tier>,
                drop: Boolean
            ) : BlockBreak(EBlockBreakType.INSTANT, hardness, tier.getOrNull(), drop) {
                companion object {
                    val CODEC = RecordCodecBuilder.create<Instant> { it.group(
                        ValueRange.CODEC.strictOptionalFieldOf("hardness", ValueRange.DEFAULT).forGetter(Instant::hardness),
                        TierSortingRegistryCodec.strictOptionalFieldOf("tier").forGetter { Optional.ofNullable(it.tier) },
                        Codec.BOOL.strictOptionalFieldOf("drop", false).forGetter(Instant::drop)
                    ).apply(it, ::Instant) }
                }
            }

            class Count(
                val count: Int,
                hardness: ValueRange,
                tier: Optional<Tier>,
                drop: Boolean
            ) : BlockBreak(EBlockBreakType.COUNT, hardness, tier.getOrNull(), drop) {
                companion object {
                    val CODEC = RecordCodecBuilder.create<Count> { it.group(
                        Codec.INT.fieldOf("count").forGetter(Count::count),
                        ValueRange.CODEC.strictOptionalFieldOf("hardness", ValueRange.DEFAULT).forGetter(Count::hardness),
                        TierSortingRegistryCodec.strictOptionalFieldOf("tier").forGetter { Optional.ofNullable(it.tier) },
                        Codec.BOOL.strictOptionalFieldOf("drop", false).forGetter(Count::drop)
                    ).apply(it, ::Count) }
                }
            }

            class FixedDamage(
                val damage: Float,
                val accumulate: Boolean,
                hardness: ValueRange,
                tier: Optional<Tier>,
                drop: Boolean
            ) : BlockBreak(EBlockBreakType.FIXED_DAMAGE, hardness, tier.getOrNull(), drop) {
                companion object {
                    val CODEC = RecordCodecBuilder.create<FixedDamage> { it.group(
                        Codec.FLOAT.fieldOf("damage").forGetter(FixedDamage::damage),
                        Codec.BOOL.strictOptionalFieldOf("accumulate", true).forGetter(FixedDamage::accumulate),
                        ValueRange.CODEC.strictOptionalFieldOf("hardness", ValueRange.DEFAULT).forGetter(FixedDamage::hardness),
                        TierSortingRegistryCodec.strictOptionalFieldOf("tier").forGetter { Optional.ofNullable(it.tier) },
                        Codec.BOOL.strictOptionalFieldOf("drop", false).forGetter(FixedDamage::drop)
                    ).apply(it, ::FixedDamage) }
                }
            }

            class DynamicDamage(
                val modifier: Float,
                val multiplier: Float,
                val accumulate: Boolean,
                hardness: ValueRange,
                tier: Optional<Tier>,
                drop: Boolean
            ) : BlockBreak(EBlockBreakType.DYNAMIC_DAMAGE, hardness, tier.getOrNull(), drop) {
                companion object {
                    val CODEC = RecordCodecBuilder.create<DynamicDamage> { it.group(
                        Codec.FLOAT.strictOptionalFieldOf("modifier", 0.0F).forGetter(DynamicDamage::modifier),
                        Codec.FLOAT.strictOptionalFieldOf("multiplier", 1.0F).forGetter(DynamicDamage::multiplier),
                        Codec.BOOL.strictOptionalFieldOf("accumulate", true).forGetter(DynamicDamage::accumulate),
                        ValueRange.CODEC.strictOptionalFieldOf("hardness", ValueRange.DEFAULT).forGetter(DynamicDamage::hardness),
                        TierSortingRegistryCodec.strictOptionalFieldOf("tier").forGetter { Optional.ofNullable(it.tier) },
                        Codec.BOOL.strictOptionalFieldOf("drop", false).forGetter(DynamicDamage::drop)
                    ).apply(it, ::DynamicDamage) }
                }
            }

            companion object {
                val CODEC = EBlockBreakType.CODEC.dispatch(BlockBreak::type) { it.codecProvider() }
            }
        }

        companion object {
            val DEFAULT = Block(emptyList(), emptyList(), BlockBreak.Never, Pierce.Never, GunPierce(false, false))
            val CODEC = RecordCodecBuilder.create { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", DEFAULT.target).forGetter(Block::target),
                Codec.list(BlockTestable.CODEC).strictOptionalFieldOf("blocks", DEFAULT.blocks).forGetter(Block::blocks),
                BlockBreak.CODEC.strictOptionalFieldOf("block_break", DEFAULT.blockBreak).forGetter(Block::blockBreak),
                Pierce.CODEC.strictOptionalFieldOf("pierce", DEFAULT.pierce).forGetter(Block::pierce),
                GunPierce.codec(false).strictOptionalFieldOf("gun_pierce", DEFAULT.gunPierce).forGetter(Block::gunPierce)
            ).apply(it, ::Block) }
        }
    }

    class Entity(
        target: List<Target>,
        val entities: List<EntityTestable>,
        val damage: EntityDamage,
        pierce: Pierce,
        gunPierce: GunPierce
    ) : BulletInteraction(EBulletInteractionType.ENTITY, target, pierce, gunPierce) {
        class EntityDamage(
            val modifier: Float,
            val multiplier: Float
        ) {
            companion object {
                val CODEC = RecordCodecBuilder.create<EntityDamage> { it.group(
                    Codec.FLOAT.strictOptionalFieldOf("modifier", 0.0F).forGetter(EntityDamage::modifier),
                    Codec.FLOAT.strictOptionalFieldOf("multiplier", 1.0F).forGetter(EntityDamage::multiplier)
                ).apply(it, ::EntityDamage) }
            }
        }

        companion object {
            val DEFAULT = Entity(emptyList(), emptyList(), EntityDamage(0.0F, 1.0F), Pierce.Default(false, 0.0F, 1.0F), GunPierce(true, true))
            val CODEC = RecordCodecBuilder.create { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", DEFAULT.target).forGetter(Entity::target),
                Codec.list(EntityTestable.CODEC).strictOptionalFieldOf("entities", DEFAULT.entities).forGetter(Entity::entities),
                EntityDamage.CODEC.strictOptionalFieldOf("damage", DEFAULT.damage).forGetter(Entity::damage),
                Pierce.CODEC.strictOptionalFieldOf("pierce", DEFAULT.pierce).forGetter(Entity::pierce),
                GunPierce.codec(true).strictOptionalFieldOf("gun_pierce", DEFAULT.gunPierce).forGetter(Entity::gunPierce)
            ).apply(it, ::Entity) }
        }
    }

    companion object {
        // Load order stuff
        init { Target; Pierce; BlockBreak }
        val CODEC = EBulletInteractionType.CODEC.dispatch(BulletInteraction::type) { it.codecProvider() }
    }
}