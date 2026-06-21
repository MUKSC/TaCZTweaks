package me.muksc.tacztweaks.feature.datapack.legacy.core

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import me.muksc.tacztweaks.core.codec.unwrapEither
import net.minecraft.world.entity.Entity

sealed interface EntityTestable {
    fun test(entity: Entity): Boolean

    companion object {
        val CODEC: Codec<EntityTestable> = Codec.either(EntityOrEntityTag.CODEC, EntityTarget.CODEC)
            .xmap(::unwrapEither) { when (it) {
                is EntityOrEntityTag -> Either.left(it)
                is EntityTarget -> Either.right(it)
            } }
    }
}