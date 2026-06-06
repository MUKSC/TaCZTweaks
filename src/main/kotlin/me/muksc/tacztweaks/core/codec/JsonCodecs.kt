package me.muksc.tacztweaks.core.codec

//? if >=1.21 {
/*import com.mojang.serialization.Codec
import net.minecraft.advancements.critereon.BlockPredicate
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.advancements.critereon.MinMaxBounds
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders

val NumberProviderCodec: Codec<NumberProvider> = NumberProviders.CODEC

val IntsMinMaxBoundsCodec: Codec<MinMaxBounds.Ints> = MinMaxBounds.Ints.CODEC

val DoublesMinMaxBoundsCodec: Codec<MinMaxBounds.Doubles> = MinMaxBounds.Doubles.CODEC

val LootItemConditionCodec: Codec<LootItemCondition> = LootItemCondition.DIRECT_CODEC

val ItemPredicateCodec: Codec<ItemPredicate> = ItemPredicate.CODEC

val BlockPredicateCodec: Codec<BlockPredicate> = BlockPredicate.CODEC

val EntityPredicateCodec: Codec<EntityPredicate> = EntityPredicate.CODEC
*///?} else {
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.advancements.critereon.BlockPredicate
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.advancements.critereon.MinMaxBounds
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.level.storage.loot.Deserializers
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider

private val conditionGson: Gson = Deserializers.createConditionSerializer().create()

val NumberProviderCodec: Codec<NumberProvider> = ExtraCodecs.JSON.comapFlatMap({
    try {
        DataResult.success(conditionGson.fromJson(it, NumberProvider::class.java))
    } catch (e: JsonParseException) {
        DataResult.error(e::message)
    }
}, {
    conditionGson.toJsonTree(it)
})

val IntsMinMaxBoundsCodec: Codec<MinMaxBounds.Ints> = ExtraCodecs.JSON.xmap(
    { MinMaxBounds.Ints.fromJson(it) },
    { it.serializeToJson() }
)

val DoublesMinMaxBoundsCodec: Codec<MinMaxBounds.Doubles> = ExtraCodecs.JSON.xmap(
    { MinMaxBounds.Doubles.fromJson(it) },
    { it.serializeToJson() }
)

val LootItemConditionCodec: Codec<LootItemCondition> = ExtraCodecs.JSON.comapFlatMap({
    try {
        DataResult.success(conditionGson.fromJson(it, LootItemCondition::class.java))
    } catch (e: JsonParseException) {
        DataResult.error(e::message)
    }
}, {
    conditionGson.toJsonTree(it)
})

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
//?}