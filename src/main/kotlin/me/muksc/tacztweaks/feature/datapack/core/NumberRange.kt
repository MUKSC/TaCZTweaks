package me.muksc.tacztweaks.feature.datapack.core

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.core.codec.NumberProviderCodec
import me.muksc.tacztweaks.core.codec.strictOptionalFieldOf
import me.muksc.tacztweaks.core.toImmutableSet
import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParam
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider
import java.util.*
import java.util.function.Function
import kotlin.jvm.optionals.getOrNull

abstract class AdvancedMinMaxBounds(
    min: Optional<Bound>,
    max: Optional<Bound>
) {
    val min: Bound? = min.getOrNull()
    val max: Bound? = max.getOrNull()

    class Bound(
        val value: NumberProvider,
        val exclusive: Boolean
    ) {
        constructor(value: NumberProvider, bound: EBound) : this(value, bound == EBound.EXCLUSIVE)

        fun lower(context: LootContext, value: Float): Boolean = if (exclusive) {
            value > this.value.getFloat(context)
        } else {
            value >= this.value.getFloat(context)
        }

        fun upper(context: LootContext, value: Float): Boolean = if (exclusive) {
            value < this.value.getFloat(context)
        } else {
            value <= this.value.getFloat(context)
        }

        fun lowerSqr(context: LootContext, value: Float): Boolean = if (exclusive) {
            value > this.value.getFloat(context).let { it * it }
        } else {
            value >= this.value.getFloat(context).let { it * it }
        }

        fun upperSqr(context: LootContext, value: Float): Boolean = if (exclusive) {
            value < this.value.getFloat(context).let { it * it }
        } else {
            value <= this.value.getFloat(context).let { it * it }
        }

        fun lower(context: LootContext, value: Int): Boolean = if (exclusive) {
            value > this.value.getInt(context)
        } else {
            value >= this.value.getInt(context)
        }

        fun upper(context: LootContext, value: Int): Boolean = if (exclusive) {
            value < this.value.getInt(context)
        } else {
            value <= this.value.getInt(context)
        }

        fun lowerSqr(context: LootContext, value: Int): Boolean = if (exclusive) {
            value > this.value.getInt(context).let { it * it }
        } else {
            value >= this.value.getInt(context).let { it * it }
        }

        fun upperSqr(context: LootContext, value: Int): Boolean = if (exclusive) {
            value < this.value.getInt(context).let { it * it }
        } else {
            value <= this.value.getInt(context).let { it * it }
        }

        enum class EBound : StringRepresentable {
            EXCLUSIVE,
            INCLUSIVE;

            override fun getSerializedName(): String = name.lowercase()

            companion object {
                val CODEC: Codec<EBound> = StringRepresentable.fromEnum(::values)
            }
        }

        companion object {
            val DIRECT_CODEC: Codec<Bound> = RecordCodecBuilder.create { it.group(
                NumberProviderCodec.fieldOf("value").forGetter(Bound::value),
                EBound.CODEC.strictOptionalFieldOf("bound", EBound.INCLUSIVE).forGetter { bound ->
                    if (bound.exclusive) EBound.EXCLUSIVE else EBound.INCLUSIVE
                }
            ).apply(it, ::Bound) }
            val CODEC: Codec<Bound> = Codec.either(DIRECT_CODEC, NumberProviderCodec).xmap({
                it.map(Function.identity()) { value -> Bound(value, EBound.INCLUSIVE) }
            }, { Either.left(it) })

            fun exclusive(value: NumberProvider): Bound = Bound(value, true)

            fun inclusive(value: NumberProvider): Bound = Bound(value, false)
        }
    }

    fun getReferencedContextParams(): Set<LootContextParam<*>> = buildSet<LootContextParam<*>> {
        if (min != null) addAll(min.value.referencedContextParams)
        if (max != null) addAll(max.value.referencedContextParams)
    }.toImmutableSet()

    fun isAny(): Boolean = min == null && max == null

    class Floats(min: Optional<Bound>, max: Optional<Bound>) : AdvancedMinMaxBounds(min, max) {
        fun matches(context: LootContext, value: Float) =
            (min == null || min.lower(context, value)) && (max == null || max.upper(context, value))

        fun matchesSqr(context: LootContext, value: Float) =
            (min == null || min.lowerSqr(context, value)) && (max == null || max.upperSqr(context, value))

        companion object {
            val CODEC: Codec<Floats> = RecordCodecBuilder.create { it.group(
                Bound.CODEC.strictOptionalFieldOf("min").forGetter(Floats::min),
                Bound.CODEC.strictOptionalFieldOf("max").forGetter(Floats::max)
            ).apply(it, ::Floats) }
        }
    }

    class Ints(min: Optional<Bound>, max: Optional<Bound>) : AdvancedMinMaxBounds(min, max) {
        fun matches(context: LootContext, value: Int) =
            (min == null || min.lower(context, value)) && (max == null || max.upper(context, value))

        fun matchesSqr(context: LootContext, value: Int) =
            (min == null || min.lowerSqr(context, value)) && (max == null || max.upperSqr(context, value))

        companion object {
            val CODEC: Codec<Ints> = RecordCodecBuilder.create { it.group(
                Bound.CODEC.strictOptionalFieldOf("min").forGetter(Ints::min),
                Bound.CODEC.strictOptionalFieldOf("max").forGetter(Ints::max)
            ).apply(it, ::Ints) }
        }
    }
}