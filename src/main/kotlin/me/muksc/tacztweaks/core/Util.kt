package me.muksc.tacztweaks.core

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet

fun String.camelToSnakeCase(): String = buildString {
    for ((index, char) in this@camelToSnakeCase.withIndex()) {
        if (char.isUpperCase() && index > 0) append('_')
        append(char.lowercaseChar())
    }
}

fun <T1, T2, R> ((T1, T2) -> R).reverse(): (T2, T1) -> R = { t2, t1 -> invoke(t1, t2) }

inline fun <T> Collection<T>.anyOrEmpty(predicate: (T) -> Boolean): Boolean = isEmpty() || any(predicate)

fun <K, V> Map<K, V>.toImmutableMap(): ImmutableMap<K, V> =
    ImmutableMap.copyOf(this)

fun <E> Set<E>.toImmutableSet(): ImmutableSet<E> =
    ImmutableSet.copyOf(this)