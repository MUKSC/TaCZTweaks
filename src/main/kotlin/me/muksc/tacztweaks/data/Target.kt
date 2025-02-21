package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.tacz.guns.entity.EntityKineticBullet
import me.muksc.tacztweaks.DispatchCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3

sealed class Target<T>(
    val type: EType,
    val values: List<T>
) {
    abstract fun test(entity: EntityKineticBullet, location: Vec3): Boolean

    class ValueRange(
        val min: Double,
        val max: Double
    ) : ClosedFloatingPointRange<Double> by min..max {
        companion object {
            val CODEC = RecordCodecBuilder.create<ValueRange> { it.group(
                Codec.DOUBLE.optionalFieldOf("min", Double.MIN_VALUE).forGetter(ValueRange::min),
                Codec.DOUBLE.optionalFieldOf("max", Double.MAX_VALUE).forGetter(ValueRange::max)
            ).apply(it, ::ValueRange) }
        }
    }

    enum class EType(
        override val key: String,
        override val codec: Codec<out Target<*>>
    ) : DispatchCodec<Target<*>> {
        FALLBACK("fallback", Fallback.CODEC),
        GUN("gun", Gun.CODEC),
        AMMO("ammo", Ammo.CODEC),
        DAMAGE("damage", Damage.CODEC),
        SPEED("speed", Speed.CODEC);

        companion object {
            private val map = EType.entries.associateBy(EType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    object Fallback : Target<Unit>(EType.FALLBACK, emptyList()) {
        override fun test(entity: EntityKineticBullet, location: Vec3): Boolean = false

        val CODEC = Codec.unit(Fallback)
    }

    class Gun(values: List<ResourceLocation>) : Target<ResourceLocation>(EType.GUN, values) {
        override fun test(entity: EntityKineticBullet, location: Vec3): Boolean =
            values.contains(entity.gunId)

        companion object {
            val CODEC = RecordCodecBuilder.create<Gun> { it.group(
                Codec.list(ResourceLocation.CODEC).optionalFieldOf("values", emptyList()).forGetter(Gun::values)
            ).apply(it, ::Gun) }
        }
    }

    class Ammo(values: List<ResourceLocation>) : Target<ResourceLocation>(EType.AMMO, values) {
        override fun test(entity: EntityKineticBullet, location: Vec3): Boolean =
            values.contains(entity.ammoId)

        companion object {
            val CODEC = RecordCodecBuilder.create<Ammo> { it.group(
                Codec.list(ResourceLocation.CODEC).optionalFieldOf("values", emptyList()).forGetter(Ammo::values)
            ).apply(it, ::Ammo) }
        }
    }

    class Damage(values: List<ValueRange>) : Target<ValueRange>(EType.DAMAGE, values) {
        override fun test(entity: EntityKineticBullet, location: Vec3): Boolean =
            values.any { it.contains(entity.getDamage(location)) }

        companion object {
            val CODEC = RecordCodecBuilder.create<Damage> { it.group(
                Codec.list(ValueRange.CODEC).optionalFieldOf("values", emptyList()).forGetter(Damage::values)
            ).apply(it, ::Damage) }
        }
    }

    class Speed(values: List<ValueRange>) : Target<ValueRange>(EType.SPEED, values) {
        override fun test(entity: EntityKineticBullet, location: Vec3): Boolean =
            values.any { it.contains(entity.deltaMovement.length() * 10) }

        companion object {
            val CODEC = RecordCodecBuilder.create<Speed> { it.group(
                Codec.list(ValueRange.CODEC).optionalFieldOf("values", emptyList()).forGetter(Speed::values)
            ).apply(it, ::Speed) }
        }
    }

    companion object {
        val CODEC = EType.CODEC.dispatch(Target<*>::type, EType::codec)
    }
}