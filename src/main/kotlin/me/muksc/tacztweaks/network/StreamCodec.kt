package me.muksc.tacztweaks.network

interface StreamCodec<T> : StreamEncoder<T>, StreamDecoder<T> {
    companion object {
        fun <T> of(encoder: StreamEncoder<T>, decoder: StreamDecoder<T>): StreamCodec<T> =
            object : StreamCodec<T>, StreamEncoder<T> by encoder, StreamDecoder<T> by decoder { }
    }
}