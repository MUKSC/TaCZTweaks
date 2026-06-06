package me.muksc.tacztweaks.feature.datapack.legacy.core

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.tacz.guns.api.TimelessAPI
import com.tacz.guns.api.entity.IGunOperator
import com.tacz.guns.entity.EntityKineticBullet
import com.tacz.guns.resource.modifier.custom.SilenceModifier
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair
import me.muksc.tacztweaks.core.codec.DispatchCodec
import me.muksc.tacztweaks.core.codec.EntityPredicateCodec
import me.muksc.tacztweaks.core.codec.IntsMinMaxBoundsCodec
import me.muksc.tacztweaks.core.codec.strictOptionalFieldOf
import me.muksc.tacztweaks.mixininterface.feature.datapack.TaCZTweaksBullet
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.MinMaxBounds
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.LivingEntity
import java.util.Locale
import kotlin.jvm.optionals.getOrNull

sealed class Target(
    val type: ETargetType
) {
    abstract fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean

    enum class ETargetType(
        override val key: String,
        override val codecProvider: () -> MapCodec<out Target>
    ) : DispatchCodec<Target> {
        ALL_OF("all_of", { AllOf.CODEC }),
        ANY_OF("any_of", { AnyOf.CODEC }),
        INVERTED("inverted", { Inverted.CODEC }),
        GUN("gun", { Gun.CODEC }),
        CATEGORY("category", { Category.CODEC }),
        AMMO("ammo", { Ammo.CODEC }),
        REGEX("regex", { RegexPattern.CODEC }),
        PREDICATE("predicate", { Predicate.CODEC }),
        DAMAGE("damage", { Damage.CODEC }),
        SPEED("speed", { Speed.CODEC }),
        SILENCED("silenced", { Silenced.CODEC }),
        BURST_INDEX("burst_index", { BurstIndex.CODEC }),
        PELLET_INDEX("pellet_index", { PelletIndex.CODEC }),
        RANDOM_CHANCE("random_chance", { RandomChance.CODEC });

        companion object {
            private val map = ETargetType.entries.associateBy(ETargetType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    class AllOf(val terms: List<Target>) : Target(ETargetType.ALL_OF) {
        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean =
            terms.all { it.test(entity, weaponId, damage) }

        companion object {
            val CODEC: MapCodec<AllOf> = RecordCodecBuilder.mapCodec { it.group(
                Codec.list(Target.CODEC).fieldOf("terms").forGetter(AllOf::terms)
            ).apply(it, ::AllOf) }
        }
    }

    class AnyOf(val terms: List<Target>) : Target(ETargetType.ANY_OF) {
        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean =
            terms.any { it.test(entity, weaponId, damage) }

        companion object {
            val CODEC: MapCodec<AnyOf> = RecordCodecBuilder.mapCodec { it.group(
                Codec.list(Target.CODEC).fieldOf("terms").forGetter(AnyOf::terms)
            ).apply(it, ::AnyOf) }
        }
    }

    class Inverted(val term: Target) : Target(ETargetType.INVERTED) {
        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean =
            !term.test(entity, weaponId, damage)

        companion object {
            val CODEC: MapCodec<Inverted> = RecordCodecBuilder.mapCodec { it.group(
                Target.CODEC.fieldOf("term").forGetter(Inverted::term)
            ).apply(it, ::Inverted) }
        }
    }

    class Gun(val values: List<ResourceLocation>) : Target(ETargetType.GUN) {
        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean =
            values.contains(weaponId)

        companion object {
            val CODEC: MapCodec<Gun> = RecordCodecBuilder.mapCodec { it.group(
                Codec.list(ResourceLocation.CODEC).strictOptionalFieldOf("values", emptyList()).forGetter(Gun::values)
            ).apply(it, ::Gun) }
        }
    }

    class Category(val values: List<String>) : Target(ETargetType.CATEGORY) {
        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean {
            val index = TimelessAPI.getCommonGunIndex(weaponId).getOrNull() ?: return false
            return values.contains(index.type.lowercase(Locale.US))
        }

        companion object {
            val CODEC: MapCodec<Category> = RecordCodecBuilder.mapCodec { it.group(
                Codec.list(Codec.STRING).strictOptionalFieldOf("values", emptyList()).forGetter(Category::values)
            ).apply(it, ::Category) }
        }
    }

    class Ammo(val values: List<ResourceLocation>) : Target(ETargetType.AMMO) {
        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean =
            entity != null && values.contains(entity.ammoId)

        companion object {
            val CODEC: MapCodec<Ammo> = RecordCodecBuilder.mapCodec { it.group(
                Codec.list(ResourceLocation.CODEC).strictOptionalFieldOf("values", emptyList()).forGetter(Ammo::values)
            ).apply(it, ::Ammo) }
        }
    }

    class RegexPattern(val match: EMatchType, val regex: Regex) : Target(ETargetType.REGEX) {
        enum class EMatchType : StringRepresentable {
            GUN,
            AMMO;

            override fun getSerializedName(): String = name.lowercase()

            companion object {
                val CODEC: Codec<EMatchType> = StringRepresentable.fromEnum(::values)
            }
        }

        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean = regex.matches(when (match) {
            EMatchType.GUN -> weaponId.toString()
            EMatchType.AMMO -> entity?.ammoId?.toString() ?: ""
        })

        companion object {
            val CODEC: MapCodec<RegexPattern> = RecordCodecBuilder.mapCodec { it.group(
                EMatchType.CODEC.fieldOf("match").forGetter(RegexPattern::match),
                Codec.STRING.xmap(::Regex, Regex::pattern).fieldOf("regex").forGetter(RegexPattern::regex)
            ).apply(it, ::RegexPattern) }
        }
    }

    class Predicate(val predicate: EntityPredicate) : Target(ETargetType.PREDICATE) {
        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean =
            entity != null && predicate.matches(entity.level() as ServerLevel, entity.position(), entity)

        companion object {
            val CODEC: MapCodec<Predicate> = RecordCodecBuilder.mapCodec { it.group(
                EntityPredicateCodec.fieldOf("predicate").forGetter(Predicate::predicate)
            ).apply(it, ::Predicate) }
        }
    }

    class Damage(val values: List<ValueRange>) : Target(ETargetType.DAMAGE) {
        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean =
            entity != null && values.any { it.contains(damage) }

        companion object {
            val CODEC: MapCodec<Damage> = RecordCodecBuilder.mapCodec { it.group(
                Codec.list(ValueRange.CODEC).strictOptionalFieldOf("values", emptyList()).forGetter(Damage::values)
            ).apply(it, ::Damage) }
        }
    }

    class Speed(val values: List<ValueRange>) : Target(ETargetType.SPEED) {
        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean =
            entity != null && values.any { it.contains(entity.deltaMovement.length() * 10) }

        companion object {
            val CODEC: MapCodec<Speed> = RecordCodecBuilder.mapCodec { it.group(
                Codec.list(ValueRange.CODEC).strictOptionalFieldOf("values", emptyList()).forGetter(Speed::values)
            ).apply(it, ::Speed) }
        }
    }

    object Silenced : Target(ETargetType.SILENCED) {
        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean {
            val owner = entity?.owner as? LivingEntity ?: return false
            val operator = IGunOperator.fromLivingEntity(owner)
            val silence = operator.cacheProperty?.getCache<ObjectObjectImmutablePair<Integer, Boolean>>(SilenceModifier.ID) ?: return false
            return silence.right()
        }

        val CODEC: MapCodec<Silenced> = MapCodec.unit(Silenced)
    }

    class BurstIndex(
        val index: MinMaxBounds.Ints
    ) : Target(ETargetType.BURST_INDEX) {
        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean {
            val ext = TaCZTweaksBullet.of(entity) ?: return false
            return index.matches(ext.`tacztweaks$getBurstIndex`())
        }

        companion object {
            val CODEC: MapCodec<BurstIndex> = RecordCodecBuilder.mapCodec { it.group(
                IntsMinMaxBoundsCodec.fieldOf("index").forGetter(BurstIndex::index)
            ).apply(it, ::BurstIndex) }
        }
    }

    class PelletIndex(
        val index: MinMaxBounds.Ints
    ) : Target(ETargetType.PELLET_INDEX) {
        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean {
            val ext = TaCZTweaksBullet.of(entity) ?: return false
            return index.matches(ext.`tacztweaks$getPelletIndex`())
        }

        companion object {
            val CODEC: MapCodec<PelletIndex> = RecordCodecBuilder.mapCodec { it.group(
                IntsMinMaxBoundsCodec.fieldOf("index").forGetter(PelletIndex::index)
            ).apply(it, ::PelletIndex) }
        }
    }

    class RandomChance(val chance: Float) : Target(ETargetType.RANDOM_CHANCE) {
        override fun test(entity: EntityKineticBullet?, weaponId: ResourceLocation, damage: Float): Boolean =
            entity != null && entity.random.nextFloat() < chance

        companion object {
            val CODEC: MapCodec<RandomChance> = RecordCodecBuilder.mapCodec { it.group(
                Codec.FLOAT.fieldOf("chance").forGetter(RandomChance::chance)
            ).apply(it, ::RandomChance) }
        }
    }

    companion object {
        val CODEC: Codec<Target> = ETargetType.CODEC.dispatch(Target::type) { it.codec() }
    }
}