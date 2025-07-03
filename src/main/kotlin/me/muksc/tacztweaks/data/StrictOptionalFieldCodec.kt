package me.muksc.tacztweaks.data

import com.mojang.serialization.*
import java.util.*
import java.util.stream.Stream

class StrictOptionalFieldCodec<A : Any>(
    private val name: String,
    private val elementCodec: Codec<A>
) : MapCodec<Optional<A>>() {
    override fun <T : Any> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<Optional<A>> {
        val value = input.get(name) ?: return DataResult.success(Optional.empty())
        val parsed = elementCodec.parse(ops, value)
        return parsed.map(Optional<A>::of).setPartial(parsed.resultOrPartial { })
    }

    override fun <T : Any> encode(input: Optional<A>, ops: DynamicOps<T>, prefix: RecordBuilder<T>): RecordBuilder<T> {
        if (input.isPresent) return prefix.add(name, elementCodec.encodeStart(ops, input.get()))
        return prefix
    }

    override fun <T : Any> keys(ops: DynamicOps<T>): Stream<T> =
        Stream.of(ops.createString(name))

    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (other !is StrictOptionalFieldCodec<*>) return false
        return name == other.name && elementCodec == other.elementCodec
    }

    override fun hashCode(): Int =
        Objects.hash(name, elementCodec)

    override fun toString(): String =
        "StrictOptionalFieldCodec[$name: $elementCodec]"
}