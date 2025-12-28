package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.DispatchCodec
import me.muksc.tacztweaks.singleOrListCodec
import me.muksc.tacztweaks.sortedBy
import me.muksc.tacztweaks.strictOptionalFieldOf
import net.minecraft.resources.ResourceLocation
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

sealed class BulletSounds(
    val type: EBulletSoundsType,
    val target: List<Target>,
    val priority: Int
) {
    class Sound(
        val sound: ResourceLocation,
        val volume: Float,
        val pitch: Float,
        val range: Float?
    ) {
        constructor(sound: ResourceLocation, volume: Float, pitch: Float, range: Optional<Float>) : this(sound, volume, pitch, range.getOrNull())

        companion object {
            val CODEC = RecordCodecBuilder.create<Sound> { it.group(
                ResourceLocation.CODEC.fieldOf("sound").forGetter(Sound::sound),
                Codec.FLOAT.strictOptionalFieldOf("volume", 1.0F).forGetter(Sound::volume),
                Codec.FLOAT.strictOptionalFieldOf("pitch", 1.0F).forGetter(Sound::pitch),
                Codec.FLOAT.strictOptionalFieldOf("range").forGetter { Optional.ofNullable(it.range) }
            ).apply(it, ::Sound) }
        }
    }

    class DistanceSound(
        val threshold: Double,
        val sound: List<Sound>
    ) {
        companion object {
            val CODEC = RecordCodecBuilder.create<DistanceSound> { it.group(
                Codec.DOUBLE.fieldOf("threshold").forGetter(DistanceSound::threshold),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("sound", emptyList()).forGetter(DistanceSound::sound)
            ).apply(it, ::DistanceSound) }
        }
    }

    enum class EBulletSoundsType(
        override val key: String,
        override val codecProvider: () -> Codec<out BulletSounds>
    ) : DispatchCodec<BulletSounds> {
        BLOCK("block", { Block.CODEC }),
        ENTITY("entity", { Entity.CODEC }),
        CONSTANT("constant", { Constant.CODEC }),
        WHIZZ("whizz", { Whizz.CODEC }),
        AIRSPACE("airspace", { AirSpace.CODEC });

        companion object {
            private val map = EBulletSoundsType.entries.associateBy(EBulletSoundsType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    class Block(
        target: List<Target>,
        val blocks: List<BlockTestable>,
        val hit: List<Sound>,
        val pierce: List<Sound>,
        val `break`: List<Sound>,
        priority: Int
    ) : BulletSounds(EBulletSoundsType.BLOCK, target, priority) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Block> { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Block::target),
                Codec.list(BlockTestable.CODEC).strictOptionalFieldOf("blocks", emptyList()).forGetter(Block::blocks),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("hit", emptyList()).forGetter(Block::hit),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("pierce", emptyList()).forGetter(Block::pierce),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("break", emptyList()).forGetter(Block::`break`),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Block::priority)
            ).apply(it, ::Block) }
        }
    }

    class Entity(
        target: List<Target>,
        val entities: List<EntityTestable>,
        val hit: List<Sound>,
        val pierce: List<Sound>,
        val kill: List<Sound>,
        priority: Int
    ) : BulletSounds(EBulletSoundsType.ENTITY, target, priority) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Entity> { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Entity::target),
                Codec.list(EntityTestable.CODEC).fieldOf("entities").forGetter(Entity::entities),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("hit", emptyList()).forGetter(Entity::hit),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("pierce", emptyList()).forGetter(Entity::pierce),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("kill", emptyList()).forGetter(Entity::kill),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Entity::priority)
            ).apply(it, ::Entity) }
        }
    }

    class Constant(
        target: List<Target>,
        val interval: Int,
        val sounds: List<Sound>,
        priority: Int
    ) : BulletSounds(EBulletSoundsType.CONSTANT, target, priority) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Constant> { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Constant::target),
                Codec.INT.fieldOf("interval").forGetter(Constant::interval),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("sounds", emptyList()).forGetter(Constant::sounds),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Constant::priority)
            ).apply(it, ::Constant) }
        }
    }

    class Whizz(
        target: List<Target>,
        val sounds: List<DistanceSound>,
        priority: Int
    ) : BulletSounds(EBulletSoundsType.WHIZZ, target, priority) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Whizz> { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Whizz::target),
                Codec.list(DistanceSound.CODEC).sortedBy(DistanceSound::threshold).strictOptionalFieldOf("sounds", emptyList()).forGetter(Whizz::sounds),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Whizz::priority)
            ).apply(it, ::Whizz) }
        }
    }

    class AirSpace(
        target: List<Target>,
        val airspace: ValueRange,
        val occlusion: ValueRange,
        val reflectivity: ValueRange,
        val sounds: List<DistanceSound>,
        priority: Int
    ) : BulletSounds(EBulletSoundsType.AIRSPACE, target, priority) {
        companion object {
            val CODEC = RecordCodecBuilder.create<AirSpace> { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(AirSpace::target),
                ValueRange.CODEC.optionalFieldOf("airspace", ValueRange.DEFAULT).forGetter(AirSpace::airspace),
                ValueRange.CODEC.optionalFieldOf("occlusion", ValueRange.DEFAULT).forGetter(AirSpace::occlusion),
                ValueRange.CODEC.optionalFieldOf("reflectivity", ValueRange.DEFAULT).forGetter(AirSpace::reflectivity),
                Codec.list(DistanceSound.CODEC).sortedBy(DistanceSound::threshold).strictOptionalFieldOf("sounds", emptyList()).forGetter(AirSpace::sounds),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(AirSpace::priority)
            ).apply(it, ::AirSpace) }
        }
    }

    companion object {
        val CODEC = EBulletSoundsType.CODEC.dispatch(BulletSounds::type) { it.codecProvider() }
    }
}