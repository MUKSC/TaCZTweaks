package me.muksc.tacztweaks.core.codec

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import java.util.function.Function
import kotlin.collections.first
import kotlin.collections.sortedBy

fun <T> singleOrListCodec(codec: Codec<T>): Codec<List<T>> =
    Codec.either(codec, Codec.list(codec))
        .xmap({ it.map(::listOf, Function.identity()) }, { when {
            it.size == 1 -> Either.left(it.first())
            else -> Either.right(it)
        } })

fun <T, R : Comparable<R>> Codec<List<T>>.sortedBy(selector: (T) -> R): Codec<List<T>> =
    xmap(Function.identity()) { it.sortedBy(selector) }