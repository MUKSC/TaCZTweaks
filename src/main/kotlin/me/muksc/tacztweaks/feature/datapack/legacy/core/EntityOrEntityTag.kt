package me.muksc.tacztweaks.feature.datapack.legacy.core

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import me.muksc.tacztweaks.core.registry.PlatformRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType

//? if forge
import me.muksc.tacztweaks.core.registry.PlatformRegistries.byNameCodec

sealed interface EntityOrEntityTag : EntityTestable {
    class Entity(val type: EntityType<*>) : EntityOrEntityTag {
        override fun test(entity: net.minecraft.world.entity.Entity): Boolean = entity.type == type

        companion object {
            val CODEC: Codec<Entity> = PlatformRegistries.ENTITY_TYPE.byNameCodec().xmap(::Entity, Entity::type)
        }
    }

    class EntityTag(val tag: TagKey<EntityType<*>>) : EntityOrEntityTag {
        override fun test(entity: net.minecraft.world.entity.Entity): Boolean = entity.type.`is`(tag)

        companion object {
            val CODEC: Codec<EntityTag> = TagKey.hashedCodec(Registries.ENTITY_TYPE).xmap(::EntityTag, EntityTag::tag)
        }
    }

    companion object {
        val CODEC: Codec<EntityOrEntityTag> = Codec.either(Entity.CODEC, EntityTag.CODEC)
            .xmap({ value -> value.map({ it as EntityOrEntityTag }) { it as EntityOrEntityTag } }) { when (it) {
                is Entity -> Either.left(it)
                is EntityTag -> Either.right(it)
            } }
    }
}