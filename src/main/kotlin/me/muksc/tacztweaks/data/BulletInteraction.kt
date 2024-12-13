package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.DispatchCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable

class BulletInteraction(
    val blocks: List<BulletInteractionBlock>,
    val guns: List<ResourceLocation>,
    val blockBreak: BlockBreak,
    val pierce: Pierce,
    val drop: Boolean
) {
    sealed interface BlockBreak {
        val type: EBlockBreakType

        enum class EBlockBreakType(
            override val key: String,
            override val codec: Codec<out BlockBreak>
        ) : DispatchCodec<BlockBreak> {
            COUNT("count", Count.CODEC),
            FIXED_DAMAGE("fixed_damage", FixedDamage.CODEC),
            DYNAMIC_DAMAGE("dynamic_damage", DynamicDamage.CODEC);

            companion object {
                private val map = entries.associateBy(EBlockBreakType::key)
                val CODEC = DispatchCodec.getCodec(map::getValue)
            }
        }

        class Count(
            val count: Int
        ) : BlockBreak {
            override val type: EBlockBreakType = EBlockBreakType.COUNT

            companion object {
                val CODEC = RecordCodecBuilder.create<Count> { it.group(
                    Codec.INT.fieldOf("count").forGetter { it.count }
                ).apply(it, ::Count) }
            }
        }

        class FixedDamage(
            val damage: Float,
            val accumulate: Boolean
        ) : BlockBreak {
            override val type: EBlockBreakType = EBlockBreakType.FIXED_DAMAGE

            companion object {
                val CODEC = RecordCodecBuilder.create<FixedDamage> { it.group(
                    Codec.FLOAT.fieldOf("damage").forGetter { it.damage },
                    Codec.BOOL.optionalFieldOf("accumulate", true).forGetter { it.accumulate }
                ).apply(it, ::FixedDamage) }
            }
        }

        class DynamicDamage(
            val modifier: Float,
            val multiplier: Float,
            val accumulate: Boolean
        ) : BlockBreak {
            override val type: EBlockBreakType = EBlockBreakType.DYNAMIC_DAMAGE

            companion object {
                val CODEC = RecordCodecBuilder.create<DynamicDamage> { it.group(
                    Codec.FLOAT.optionalFieldOf("modifier", 0.0F).forGetter { it.modifier },
                    Codec.FLOAT.optionalFieldOf("multiplier", 1.0F).forGetter { it.multiplier },
                    Codec.BOOL.optionalFieldOf("accumulate", true).forGetter { it.accumulate }
                ).apply(it, ::DynamicDamage) }
            }
        }

        companion object {
            val CODEC = EBlockBreakType.CODEC.dispatch(BlockBreak::type, EBlockBreakType::codec)
        }
    }

    sealed interface Pierce {
        val type: EPierceType

        enum class EPierceType(
            override val key: String,
            override val codec: Codec<out Pierce>
        ) : DispatchCodec<Pierce> {
            COUNT("count", Count.CODEC),
            DAMAGE("damage", Damage.CODEC);

            companion object {
                private val map = EPierceType.entries.associateBy(EPierceType::key)
                val CODEC = DispatchCodec.getCodec(map::getValue)
            }
        }

        class Count(
            val condition: ECondition,
            val count: Int,
            val damageFalloff: Float,
            val damageMultiplier: Float,
            val requireGunPierce: Boolean
        ) : Pierce {
            override val type: EPierceType = EPierceType.COUNT

            companion object {
                val CODEC = RecordCodecBuilder.create<Count> { it.group(
                    ECondition.CODEC.fieldOf("condition").forGetter { it.condition },
                    Codec.INT.fieldOf("count").forGetter { it.count },
                    Codec.FLOAT.optionalFieldOf("damage_falloff", 0.0F).forGetter { it.damageFalloff },
                    Codec.FLOAT.optionalFieldOf("damage_multiplier", 1.0F).forGetter { it.damageMultiplier },
                    Codec.BOOL.optionalFieldOf("require_gun_pierce", false).forGetter { it.requireGunPierce }
                ).apply(it, ::Count) }
            }
        }

        class Damage(
            val condition: ECondition,
            val damageFalloff: Float,
            val damageMultiplier: Float,
            val requireGunPierce: Boolean
        ) : Pierce {
            override val type: EPierceType = EPierceType.DAMAGE

            companion object {
                val CODEC = RecordCodecBuilder.create<Damage> { it.group(
                    ECondition.CODEC.fieldOf("condition").forGetter { it.condition },
                    Codec.FLOAT.optionalFieldOf("damage_falloff", 0.0F).forGetter { it.damageFalloff },
                    Codec.FLOAT.optionalFieldOf("damage_multiplier", 1.0F).forGetter { it.damageMultiplier },
                    Codec.BOOL.optionalFieldOf("require_gun_pierce", false).forGetter { it.requireGunPierce }
                ).apply(it, ::Damage) }
            }
        }

        enum class ECondition : StringRepresentable {
            ALWAYS,
            ON_BREAK;

            override fun getSerializedName(): String = name.lowercase()

            companion object {
                val CODEC = StringRepresentable.fromEnum(::values)
            }
        }

        companion object {
            val CODEC = EPierceType.CODEC.dispatch(Pierce::type, EPierceType::codec)
        }
    }

    companion object {
        val CODEC = RecordCodecBuilder.create<BulletInteraction> { it.group(
            Codec.list(BulletInteractionBlock.CODEC).fieldOf("blocks").forGetter { it.blocks },
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("guns", emptyList()).forGetter { it.guns },
            BlockBreak.CODEC.fieldOf("block_break").forGetter { it.blockBreak },
            Pierce.CODEC.fieldOf("pierce").forGetter { it.pierce },
            Codec.BOOL.optionalFieldOf("drop", false).forGetter { it.drop }
        ).apply(it, ::BulletInteraction) }
    }
}