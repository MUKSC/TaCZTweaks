package me.muksc.tacztweaks.feature.datapack.legacy.core

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.core.codec.strictOptionalFieldOf

class ValueRange(
    start: Double,
    endInclusive: Double
) : ClosedFloatingPointRange<Double> by start..endInclusive {
    companion object {
        val DEFAULT = ValueRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
        val CODEC: Codec<ValueRange> = RecordCodecBuilder.create { it.group(
            Codec.DOUBLE.strictOptionalFieldOf("min", DEFAULT.start).forGetter(ValueRange::start),
            Codec.DOUBLE.strictOptionalFieldOf("max", DEFAULT.endInclusive).forGetter(ValueRange::endInclusive)
        ).apply(it, ::ValueRange) }
    }
}