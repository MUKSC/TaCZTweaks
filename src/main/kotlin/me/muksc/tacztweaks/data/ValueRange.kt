package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.strictOptionalFieldOf

class ValueRange(
    val min: Double,
    val max: Double
) : ClosedFloatingPointRange<Double> by min..max {
    companion object {
        val DEFAULT = ValueRange(Double.MIN_VALUE, Double.MAX_VALUE)
        val CODEC = RecordCodecBuilder.create<ValueRange> { it.group(
            Codec.DOUBLE.strictOptionalFieldOf("min", DEFAULT.min).forGetter(ValueRange::min),
            Codec.DOUBLE.strictOptionalFieldOf("max", DEFAULT.max).forGetter(ValueRange::max)
        ).apply(it, ::ValueRange) }
    }
}