package me.muksc.tacztweaks.data.codec

import com.mojang.serialization.Codec
import net.minecraft.advancements.critereon.MinMaxBounds
import net.minecraft.util.ExtraCodecs

val IntsMinMaxBounds: Codec<MinMaxBounds.Ints> = ExtraCodecs.JSON.xmap(
    { MinMaxBounds.Ints.fromJson(it) },
    { it.serializeToJson() }
)