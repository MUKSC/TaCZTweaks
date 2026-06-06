package me.muksc.tacztweaks.core.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec

interface DispatchCodec<T> {
    val key: String
    val codecProvider: () -> MapCodec<out T>

    //? if <1.20.5 {
    fun codec(): Codec<out T> = codecProvider().codec()
    //?} else {
    /*fun codec(): MapCodec<out T> = codecProvider()
    *///?}

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