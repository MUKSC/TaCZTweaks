package me.muksc.tacztweaks.core.codec

//? if forge {
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
//?} else {
/*import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import me.muksc.tacztweaks.core.Identifier
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Tier
import net.minecraft.world.item.Tiers

val TierSortingRegistryCodec: Codec<Tier> = ResourceLocation.CODEC.flatXmap({
    try {
        DataResult.success(Tiers.valueOf(it.path.uppercase()))
    } catch (e: IllegalArgumentException) {
        DataResult.error { e.message }
    }
}, {
    val tier = Tiers.entries.find { tier -> tier == it } ?: return@flatXmap DataResult.error { "Cannot serialize tier $it" }
    DataResult.success(Identifier(tier.name.lowercase()))
})
*///?}