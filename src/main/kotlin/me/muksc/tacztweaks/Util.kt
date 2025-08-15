package me.muksc.tacztweaks

import com.google.common.collect.ImmutableMap
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.muksc.tacztweaks.data.StrictOptionalFieldCodec
import java.util.*

fun <T> identity(value: T): T = value

fun <K, V> Map<K, V>.toImmutableMap(): ImmutableMap<K, V> =
    ImmutableMap.copyOf(this)

inline fun <T> Comparator<T>.thenPrioritizeBy(crossinline selector: (T) -> Boolean): Comparator<T> =
    thenByDescending { selector(it) }

fun <T> Class<T>.setPrivateField(instance: T, name: String, value: Any?) {
    getDeclaredField(name).run {
        isAccessible = true
        set(instance, value)
    }
}

fun <T : Any> Codec<T>.strictOptionalFieldOf(name: String): MapCodec<Optional<T>> =
    StrictOptionalFieldCodec(name, this)

fun <T : Any> Codec<T>.strictOptionalFieldOf(name: String, defaultValue: T): MapCodec<T> =
    StrictOptionalFieldCodec(name, this).xmap(
        { it.orElse(defaultValue) },
        { if (it == defaultValue) Optional.empty() else Optional.of(it) }
    )

fun <T> singleOrListCodec(codec: Codec<T>): Codec<List<T>> =
    Codec.either(codec, Codec.list(codec))
        .xmap({ it.map(::listOf, ::identity) }, { when {
            it.size == 1 -> Either.left(it.first())
            else -> Either.right(it)
        } })

fun <T, R : Comparable<R>> Codec<List<T>>.sortedBy(selector: (T) -> R): Codec<List<T>> =
    xmap(::identity) { it.sortedBy(selector) }
