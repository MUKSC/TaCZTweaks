package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.Dynamic
import com.mojang.serialization.JsonOps
import net.minecraft.advancements.critereon.ItemPredicate

val JsonCodec = Codec.PASSTHROUGH.xmap(
    { it.convert(JsonOps.INSTANCE).value },
    { Dynamic(JsonOps.INSTANCE, it) }
)

val ItemPredicateCodec = JsonCodec.xmap(
    { ItemPredicate.fromJson(it) },
    { it.serializeToJson() }
)