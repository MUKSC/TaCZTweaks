package me.muksc.tacztweaks.data.old

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.data.codec.DispatchCodec
import me.muksc.tacztweaks.data.core.BlockOrBlockTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable

class BulletInteraction(
    val blocks: List<BlockOrBlockTag>,
    val guns: List<ResourceLocation>,
    val blockBreak: BlockBreak,
    val pierce: Pierce,
    val drop: Boolean
) {
    sealed interface BlockBreak {
        val type: EBlockBreakType

        enum class EBlockBreakType(
            override val key: String,
            override val codecProvider: () -> Codec<out BlockBreak>
        ) : DispatchCodec<BlockBreak> {
            NEVER("never", { Never.CODEC }),
            COUNT("count", { Count.CODEC }),
            FIXED_DAMAGE("fixed_damage", { FixedDamage.CODEC }),
            DYNAMIC_DAMAGE("dynamic_damage", { DynamicDamage.CODEC });

            companion object {
                private val map = entries.associateBy(EBlockBreakType::key)
                val CODEC = DispatchCodec.getCodec(map::getValue)
            }
        }

        class Never : BlockBreak {
            override val type: EBlockBreakType = EBlockBreakType.NEVER

            companion object {
                val CODEC: Codec<Never> = Codec.unit(Never())
            }
        }

        class Count(
            val count: Int
        ) : BlockBreak {
            override val type: EBlockBreakType = EBlockBreakType.COUNT

            companion object {
                val CODEC: Codec<Count> = RecordCodecBuilder.create<Count> { instance -> instance.group(
                    Codec.INT.fieldOf("count").forGetter { it.count }
                ).apply(instance, ::Count) }
            }
        }

        class FixedDamage(
            val damage: Float,
            val accumulate: Boolean
        ) : BlockBreak {
            override val type: EBlockBreakType = EBlockBreakType.FIXED_DAMAGE

            companion object {
                val CODEC: Codec<FixedDamage> = RecordCodecBuilder.create<FixedDamage> { instance -> instance.group(
                    Codec.FLOAT.fieldOf("damage").forGetter { it.damage },
                    Codec.BOOL.optionalFieldOf("accumulate", true).forGetter { it.accumulate }
                ).apply(instance, ::FixedDamage) }
            }
        }

        class DynamicDamage(
            val modifier: Float,
            val multiplier: Float,
            val accumulate: Boolean
        ) : BlockBreak {
            override val type: EBlockBreakType = EBlockBreakType.DYNAMIC_DAMAGE

            companion object {
                val CODEC: Codec<DynamicDamage> = RecordCodecBuilder.create<DynamicDamage> { instance -> instance.group(
                    Codec.FLOAT.optionalFieldOf("modifier", 0.0F).forGetter { it.modifier },
                    Codec.FLOAT.optionalFieldOf("multiplier", 1.0F).forGetter { it.multiplier },
                    Codec.BOOL.optionalFieldOf("accumulate", true).forGetter { it.accumulate }
                ).apply(instance, ::DynamicDamage) }
            }
        }

        companion object {
            val CODEC: Codec<BlockBreak> = EBlockBreakType.CODEC.dispatch(BlockBreak::type) { it.codecProvider() }
        }
    }

    sealed interface Pierce {
        val type: EPierceType

        enum class EPierceType(
            override val key: String,
            override val codecProvider: () -> Codec<out Pierce>
        ) : DispatchCodec<Pierce> {
            NEVER("never", { Never.CODEC }),
            COUNT("count", { Count.CODEC }),
            DAMAGE("damage", { Damage.CODEC });

            companion object {
                private val map = EPierceType.entries.associateBy(EPierceType::key)
                val CODEC = DispatchCodec.getCodec(map::getValue)
            }
        }

        class Never : Pierce {
            override val type: EPierceType = EPierceType.NEVER

            companion object {
                val CODEC: Codec<Never> = Codec.unit(Never())
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
                val CODEC: Codec<Count> = RecordCodecBuilder.create<Count> { instance -> instance.group(
                    ECondition.CODEC.fieldOf("condition").forGetter { it.condition },
                    Codec.INT.fieldOf("count").forGetter { it.count },
                    Codec.FLOAT.optionalFieldOf("damage_falloff", 0.0F).forGetter { it.damageFalloff },
                    Codec.FLOAT.optionalFieldOf("damage_multiplier", 1.0F).forGetter { it.damageMultiplier },
                    Codec.BOOL.optionalFieldOf("require_gun_pierce", false).forGetter { it.requireGunPierce }
                ).apply(instance, ::Count) }
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
                val CODEC: Codec<Damage> = RecordCodecBuilder.create<Damage> { instance -> instance.group(
                    ECondition.CODEC.fieldOf("condition").forGetter { it.condition },
                    Codec.FLOAT.optionalFieldOf("damage_falloff", 0.0F).forGetter { it.damageFalloff },
                    Codec.FLOAT.optionalFieldOf("damage_multiplier", 1.0F).forGetter { it.damageMultiplier },
                    Codec.BOOL.optionalFieldOf("require_gun_pierce", false).forGetter { it.requireGunPierce }
                ).apply(instance, ::Damage) }
            }
        }

        enum class ECondition : StringRepresentable {
            ALWAYS,
            ON_BREAK;

            override fun getSerializedName(): String = name.lowercase()

            companion object {
                val CODEC: Codec<ECondition> = StringRepresentable.fromEnum(::values)
            }
        }

        companion object {
            val CODEC: Codec<Pierce> = EPierceType.CODEC.dispatch(Pierce::type) { it.codecProvider() }
        }
    }

    companion object {
        val CODEC: Codec<BulletInteraction> = RecordCodecBuilder.create<BulletInteraction> { instance -> instance.group(
            Codec.list(BlockOrBlockTag.CODEC).fieldOf("blocks").forGetter { it.blocks },
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("guns", emptyList()).forGetter { it.guns },
            BlockBreak.CODEC.fieldOf("block_break").forGetter { it.blockBreak },
            Pierce.CODEC.fieldOf("pierce").forGetter { it.pierce },
            Codec.BOOL.optionalFieldOf("drop", false).forGetter { it.drop }
        ).apply(instance, ::BulletInteraction) }
    }
}