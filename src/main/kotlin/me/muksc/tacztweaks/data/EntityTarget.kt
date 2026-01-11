package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.DispatchCodec
import me.muksc.tacztweaks.id
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraftforge.registries.ForgeRegistries

sealed class EntityTarget(
    val type: EEntityTargetType
) : EntityTestable {
    enum class EEntityTargetType(
        override val key: String,
        override val codecProvider: () -> Codec<out EntityTarget>
    ) : DispatchCodec<EntityTarget> {
        ALL_OF("all_of", { AllOf.CODEC }),
        ANY_OF("any_of", { AnyOf.CODEC }),
        INVERTED("inverted", { Inverted.CODEC }),
        ENTITY("entity", { Entity.CODEC }),
        ENTITY_TAG("entity_tag", { EntityTag.CODEC }),
        REGEX("regex", { RegexPattern.CODEC }),
        PREDICATE("predicate", { Predicate.CODEC }),
        HEALTH("health", { Health.CODEC }),
        ARMOR("armor", { Armor.CODEC }),
        ARMOR_TOUGHNESS("armor_toughness", { ArmorToughness.CODEC });

        companion object {
            private val map = EEntityTargetType.entries.associateBy(EEntityTargetType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    class AllOf(val terms: List<EntityTarget>) : EntityTarget(EEntityTargetType.ALL_OF) {
        override fun test(entity: net.minecraft.world.entity.Entity): Boolean =
            terms.all { it.test(entity) }

        companion object {
            val CODEC = RecordCodecBuilder.create<AllOf> { it.group(
                Codec.list(EntityTarget.CODEC).fieldOf("terms").forGetter(AllOf::terms)
            ).apply(it, ::AllOf) }
        }
    }

    class AnyOf(val terms: List<EntityTarget>) : EntityTarget(EEntityTargetType.ANY_OF) {
        override fun test(entity: net.minecraft.world.entity.Entity): Boolean =
            terms.any { it.test(entity) }

        companion object {
            val CODEC = RecordCodecBuilder.create<AnyOf> { it.group(
                Codec.list(EntityTarget.CODEC).fieldOf("terms").forGetter(AnyOf::terms)
            ).apply(it, ::AnyOf) }
        }
    }

    class Inverted(val term: EntityTarget) : EntityTarget(EEntityTargetType.INVERTED) {
        override fun test(entity: net.minecraft.world.entity.Entity): Boolean =
            !term.test(entity)

        companion object {
            val CODEC = RecordCodecBuilder.create<Inverted> { it.group(
                EntityTarget.CODEC.fieldOf("term").forGetter(Inverted::term)
            ).apply(it, ::Inverted) }
        }
    }

    class Entity(val values: List<EntityType<*>>) : EntityTarget(EEntityTargetType.ENTITY_TAG) {
        override fun test(entity: net.minecraft.world.entity.Entity): Boolean =
            values.any { entity.type == it }

        companion object {
            val CODEC = RecordCodecBuilder.create<Entity> { it.group(
                Codec.list(ForgeRegistries.ENTITY_TYPES.codec).fieldOf("values").forGetter(Entity::values)
            ).apply(it, ::Entity) }
        }
    }

    class EntityTag(val values: List<TagKey<EntityType<*>>>) : EntityTarget(EEntityTargetType.ENTITY_TAG) {
        override fun test(entity: net.minecraft.world.entity.Entity): Boolean =
            values.any { entity.type.`is`(it) }

        companion object {
            val CODEC = RecordCodecBuilder.create<EntityTag> { it.group(
                Codec.list(TagKey.hashedCodec(Registries.ENTITY_TYPE)).fieldOf("values").forGetter(EntityTag::values)
            ).apply(it, ::EntityTag) }
        }
    }

    class RegexPattern(val regex: Regex) : EntityTarget(EEntityTargetType.REGEX) {
        override fun test(entity: net.minecraft.world.entity.Entity): Boolean =
            regex.matches(entity.type.id.toString())

        companion object {
            val CODEC = RecordCodecBuilder.create<RegexPattern> { it.group(
                Codec.STRING.xmap(::Regex, Regex::pattern).fieldOf("regex").forGetter(RegexPattern::regex)
            ).apply(it, ::RegexPattern) }
        }
    }

    class Predicate(val predicate: EntityPredicate) : EntityTarget(EEntityTargetType.PREDICATE) {
        override fun test(entity: net.minecraft.world.entity.Entity): Boolean =
            predicate.matches(entity.level() as ServerLevel, entity.position(), entity)

        companion object {
            val CODEC = RecordCodecBuilder.create<Predicate> { it.group(
                EntityPredicateCodec.fieldOf("predicate").forGetter(Predicate::predicate)
            ).apply(it, ::Predicate) }
        }
    }

    class Health(val unit: EHealthUnit, val range: ValueRange) : EntityTarget(EEntityTargetType.HEALTH) {
        enum class EHealthUnit : StringRepresentable {
            RAW,
            PERCENTAGE;

            override fun getSerializedName(): String = name.lowercase()

            companion object {
                val CODEC = StringRepresentable.fromEnum(::values)
            }
        }

        override fun test(entity: net.minecraft.world.entity.Entity): Boolean =
            entity is LivingEntity && when (unit) {
                EHealthUnit.RAW -> entity.health
                EHealthUnit.PERCENTAGE -> entity.health / entity.maxHealth
            } in range

        companion object {
            val CODEC = RecordCodecBuilder.create<Health> { it.group(
                EHealthUnit.CODEC.fieldOf("unit").forGetter(Health::unit),
                ValueRange.CODEC.fieldOf("range").forGetter(Health::range)
            ).apply(it, ::Health) }
        }
    }

    class Armor(val range: ValueRange) : EntityTarget(EEntityTargetType.ARMOR) {
        override fun test(entity: net.minecraft.world.entity.Entity): Boolean =
            entity is LivingEntity && entity.getAttributeValue(Attributes.ARMOR) in range

        companion object {
            val CODEC = RecordCodecBuilder.create<Armor> { it.group(
                ValueRange.CODEC.fieldOf("range").forGetter(Armor::range)
            ).apply(it, ::Armor) }
        }
    }

    class ArmorToughness(val range: ValueRange) : EntityTarget(EEntityTargetType.ARMOR_TOUGHNESS) {
        override fun test(entity: net.minecraft.world.entity.Entity): Boolean =
            entity is LivingEntity && entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS) in range

        companion object {
            val CODEC = RecordCodecBuilder.create<ArmorToughness> { it.group(
                ValueRange.CODEC.fieldOf("range").forGetter(ArmorToughness::range)
            ).apply(it, ::ArmorToughness) }
        }
    }

    companion object {
        val CODEC = EEntityTargetType.CODEC.dispatch(EntityTarget::type) { it.codecProvider() }
    }
}