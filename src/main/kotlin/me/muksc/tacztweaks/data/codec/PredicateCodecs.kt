package me.muksc.tacztweaks.data.codec

import com.mojang.serialization.Codec
import net.minecraft.advancements.critereon.BlockPredicate
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.util.ExtraCodecs

val ItemPredicateCodec: Codec<ItemPredicate> = ExtraCodecs.JSON.xmap(
    { ItemPredicate.fromJson(it) },
    { it.serializeToJson() }
)

val BlockPredicateCodec: Codec<BlockPredicate> = ExtraCodecs.JSON.xmap(
    { BlockPredicate.fromJson(it) },
    { it.serializeToJson() }
)

val EntityPredicateCodec: Codec<EntityPredicate> = ExtraCodecs.JSON.xmap(
    { EntityPredicate.fromJson(it) },
    { it.serializeToJson() }
)