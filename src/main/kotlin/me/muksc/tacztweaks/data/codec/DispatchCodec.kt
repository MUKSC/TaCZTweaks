package me.muksc.tacztweaks.data.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult

interface DispatchCodec<T> {
    val key: String
    val codecProvider: () -> Codec<out T>

    companion object {
        fun <T : DispatchCodec<*>> getCodec(valueOf: (String) -> T): Codec<T> = Codec.STRING.comapFlatMap({
            try {
                DataResult.success(valueOf(it))
            } catch (e: IllegalArgumentException) {
                DataResult.error { e.stackTraceToString() }
            }
        }, DispatchCodec<*>::key)
    }
}