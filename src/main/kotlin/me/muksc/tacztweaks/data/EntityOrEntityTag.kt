package me.muksc.tacztweaks.data

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraftforge.registries.ForgeRegistries

sealed interface EntityOrEntityTag {
    fun test(entity: net.minecraft.world.entity.Entity): Boolean

    class Entity(val type: EntityType<*>) : EntityOrEntityTag {
        override fun test(entity: net.minecraft.world.entity.Entity): Boolean = entity.type == type

        companion object {
            val CODEC = ForgeRegistries.ENTITY_TYPES.codec.xmap(::Entity, Entity::type)
        }
    }

    class EntityTag(val tag: TagKey<EntityType<*>>) : EntityOrEntityTag {
        override fun test(entity: net.minecraft.world.entity.Entity): Boolean = entity.type.`is`(tag)

        companion object {
            val CODEC = TagKey.hashedCodec(Registries.ENTITY_TYPE).xmap(::EntityTag, EntityTag::tag)
        }
    }

    companion object {
        val CODEC = Codec.either(Entity.CODEC, EntityTag.CODEC)
            .xmap({ it.map({ it as EntityOrEntityTag }) { it as EntityOrEntityTag } }) { when (it) {
                is Entity -> Either.left(it)
                is EntityTag -> Either.right(it)
            } }
    }
}