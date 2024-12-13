package me.muksc.tacztweaks

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult

interface DispatchCodec<T> {
    val key: String
    val codec: Codec<out T>

    companion object {
        fun <T : DispatchCodec<*>> getCodec(valueOf: (String) -> T): Codec<T> = Codec.STRING.comapFlatMap({
            try {
                println("valueOf $it = ${valueOf(it)}")
                DataResult.success(valueOf(it))
            } catch (e: IllegalArgumentException) {
                DataResult.error { e.stackTraceToString() }
            }
        }, DispatchCodec<*>::key)
    }
}
