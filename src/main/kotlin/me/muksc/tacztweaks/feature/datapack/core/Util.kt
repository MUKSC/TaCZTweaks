package me.muksc.tacztweaks.feature.datapack.core

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.Optional

fun <O, A : Any> MapCodec<Optional<A>>.forGetter(getter: (O) -> A?): RecordCodecBuilder<O, Optional<A>> =
    forGetter { Optional.ofNullable(getter(it)) }