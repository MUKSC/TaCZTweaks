package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Tier
import net.minecraftforge.common.TierSortingRegistry

val TierSortingRegistryCodec: Codec<Tier> = ResourceLocation.CODEC.flatXmap({
    TierSortingRegistry.byName(it)?.let(DataResult<ResourceLocation>::success) ?: DataResult.error {
        "Unknown registry key in TierSortingRegistry: $it"
    }
}, {
    TierSortingRegistry.getName(it)?.let(DataResult<Tier>::success) ?: DataResult.error {
        "Unknown registry element in TierSortingRegistry: $it"
    }
})