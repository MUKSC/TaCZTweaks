package me.muksc.tacztweaks.registry

import com.mojang.serialization.Codec
import me.muksc.tacztweaks.feature.datapack.BulletInteraction
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.util.ExtraCodecs

object ModRegistries {
    val REGISTRIES = listOf(
        DataPackRegistry(
            BulletInteraction.REGISTRY_KEY,
            ExtraCodecs.catchDecoderException(BulletInteraction.CODEC)
        )
    )

    data class DataPackRegistry<T>(
        val key: ResourceKey<Registry<T>>,
        val codec: Codec<T>
    )
}