package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.tacz.guns.entity.EntityKineticBullet
import me.muksc.tacztweaks.DispatchCodec
import me.muksc.tacztweaks.strictOptionalFieldOf
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable
import net.minecraft.world.phys.Vec3

sealed class Target(
    val type: ETargetType
) {
    abstract fun test(entity: EntityKineticBullet, location: Vec3): Boolean

    enum class ETargetType(
        override val key: String,
        override val codecProvider: () -> Codec<out Target>
    ) : DispatchCodec<Target> {
        ALL_OF("all_of", { AllOf.CODEC }),
        ANY_OF("any_of", { AnyOf.CODEC }),
        INVERTED("inverted", { Inverted.CODEC }),
        GUN("gun", { Gun.CODEC }),
        AMMO("ammo", { Ammo.CODEC }),
        REGEX("regex", { RegexPattern.CODEC }),
        DAMAGE("damage", { Damage.CODEC }),
        SPEED("speed", { Speed.CODEC });

        companion object {
            private val map = ETargetType.entries.associateBy(ETargetType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    class AllOf(val terms: List<Target>) : Target(ETargetType.ALL_OF) {
        override fun test(entity: EntityKineticBullet, location: Vec3): Boolean =
            terms.all { it.test(entity, location) }

        companion object {
            val CODEC = RecordCodecBuilder.create<AllOf> { it.group(
                Codec.list(Target.CODEC).fieldOf("terms").forGetter(AllOf::terms)
            ).apply(it, ::AllOf) }
        }
    }

    class AnyOf(val terms: List<Target>) : Target(ETargetType.ANY_OF) {
        override fun test(entity: EntityKineticBullet, location: Vec3): Boolean =
            terms.any { it.test(entity, location) }

        companion object {
            val CODEC = RecordCodecBuilder.create<AnyOf> { it.group(
                Codec.list(Target.CODEC).fieldOf("terms").forGetter(AnyOf::terms)
            ).apply(it, ::AnyOf) }
        }
    }

    class Inverted(val value: Target) : Target(ETargetType.INVERTED) {
        override fun test(entity: EntityKineticBullet, location: Vec3): Boolean = !value.test(entity, location)

        companion object {
            val CODEC = RecordCodecBuilder.create<Inverted> { it.group(
                Target.CODEC.fieldOf("term").forGetter(Inverted::value)
            ).apply(it, ::Inverted) }
        }
    }

    class Gun(val values: List<ResourceLocation>) : Target(ETargetType.GUN) {
        override fun test(entity: EntityKineticBullet, location: Vec3): Boolean =
            values.contains(entity.gunId)

        companion object {
            val CODEC = RecordCodecBuilder.create<Gun> { it.group(
                Codec.list(ResourceLocation.CODEC).strictOptionalFieldOf("values", emptyList()).forGetter(Gun::values)
            ).apply(it, ::Gun) }
        }
    }

    class Ammo(val values: List<ResourceLocation>) : Target(ETargetType.AMMO) {
        override fun test(entity: EntityKineticBullet, location: Vec3): Boolean =
            values.contains(entity.ammoId)

        companion object {
            val CODEC = RecordCodecBuilder.create<Ammo> { it.group(
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
                val CODEC = StringRepresentable.fromEnum(::values)
            }
        }

        override fun test(entity: EntityKineticBullet, location: Vec3): Boolean = regex.matches(when (match) {
            EMatchType.GUN -> entity.gunId.toString()
            EMatchType.AMMO -> entity.ammoId.toString()
        })

        companion object {
            val CODEC = RecordCodecBuilder.create<RegexPattern> { it.group(
                EMatchType.CODEC.fieldOf("match").forGetter(RegexPattern::match),
                Codec.STRING.xmap(::Regex, Regex::pattern).fieldOf("regex").forGetter(RegexPattern::regex)
            ).apply(it, ::RegexPattern) }
        }
    }

    class Damage(val values: List<ValueRange>) : Target(ETargetType.DAMAGE) {
        override fun test(entity: EntityKineticBullet, location: Vec3): Boolean =
            values.any { it.contains(entity.getDamage(location)) }

        companion object {
            val CODEC = RecordCodecBuilder.create<Damage> { it.group(
                Codec.list(ValueRange.CODEC).strictOptionalFieldOf("values", emptyList()).forGetter(Damage::values)
            ).apply(it, ::Damage) }
        }
    }

    class Speed(val values: List<ValueRange>) : Target(ETargetType.SPEED) {
        override fun test(entity: EntityKineticBullet, location: Vec3): Boolean =
            values.any { it.contains(entity.deltaMovement.length() * 10) }

        companion object {
            val CODEC = RecordCodecBuilder.create<Speed> { it.group(
                Codec.list(ValueRange.CODEC).strictOptionalFieldOf("values", emptyList()).forGetter(Speed::values)
            ).apply(it, ::Speed) }
        }
    }

    companion object {
        val CODEC = ETargetType.CODEC.dispatch(Target::type) { it.codecProvider() }
    }
}