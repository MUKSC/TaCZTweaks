package me.muksc.tacztweaks.data

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import me.muksc.tacztweaks.identity
import net.minecraft.world.entity.Entity

sealed interface EntityTestable {
    fun test(entity: Entity): Boolean

    companion object {
        val CODEC = Codec.either(EntityOrEntityTag.CODEC, EntityTarget.CODEC)
            .xmap({ it.map(::identity, ::identity) }, { when (it) {
                is EntityOrEntityTag -> Either.left(it)
                is EntityTarget -> Either.right(it)
            } })
    }
}