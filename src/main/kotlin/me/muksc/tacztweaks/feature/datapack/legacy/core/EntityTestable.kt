package me.muksc.tacztweaks.feature.datapack.legacy.core

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import net.minecraft.world.entity.Entity
import java.util.function.Function

sealed interface EntityTestable {
    fun test(entity: Entity): Boolean

    companion object {
        val CODEC: Codec<EntityTestable> = Codec.either(EntityOrEntityTag.CODEC, EntityTarget.CODEC)
            .xmap({ it.map(Function.identity(), Function.identity()) }, { when (it) {
                is EntityOrEntityTag -> Either.left(it)
                is EntityTarget -> Either.right(it)
            } })
    }
}