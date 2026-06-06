package me.muksc.tacztweaks.core.codec

//? if >=1.20.5 {
/*import net.minecraft.network.codec.StreamCodec

typealias StreamCodec<B, V> = StreamCodec<B, V>
*///?} else {
fun interface StreamDecoder<I, T> {
    fun decode(buffer: I): T
}

fun interface StreamEncoder<O, T> {
    fun encode(buffer: O, value: T)
}

interface StreamCodec<B, V> : StreamDecoder<B, V>, StreamEncoder<B, V> {
    companion object {
        fun <B, V> of(encoder: StreamEncoder<B, V>, decoder: StreamDecoder<B, V>): StreamCodec<B, V> =
            object : StreamCodec<B, V> {
                override fun decode(buffer: B): V = decoder.decode(buffer)

                override fun encode(buffer: B, value: V) = encoder.encode(buffer, value)
            }

        fun <B, V> unit(expectedValue: V): StreamCodec<B, V> =
            object : StreamCodec<B, V> {
                override fun decode(buffer: B): V = expectedValue

                override fun encode(buffer: B, value: V) = Unit
            }
    }

    fun interface CodecOperation<B, S, T> {
        fun apply(streamCodec: StreamCodec<B, S>): StreamCodec<B, T>
    }
}
//?}