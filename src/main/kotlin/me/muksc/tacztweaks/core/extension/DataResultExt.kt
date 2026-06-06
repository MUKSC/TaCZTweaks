package me.muksc.tacztweaks.core.extension

//? if <1.20.5 {
import com.mojang.serialization.DataResult

fun <T> DataResult<T>.getOrThrow(): T = getOrThrow(false) { }

fun <T> DataResult<T>.getPartialOrThrow(): T = getOrThrow(true) { }
//?}