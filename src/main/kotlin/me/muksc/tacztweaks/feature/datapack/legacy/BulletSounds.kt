package me.muksc.tacztweaks.feature.datapack.legacy

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.core.codec.DispatchCodec
import me.muksc.tacztweaks.core.codec.singleOrListCodec
import me.muksc.tacztweaks.core.codec.sortedBy
import me.muksc.tacztweaks.core.codec.strictOptionalFieldOf
import me.muksc.tacztweaks.feature.datapack.core.forGetter
import me.muksc.tacztweaks.feature.datapack.legacy.core.BlockTestable
import me.muksc.tacztweaks.feature.datapack.legacy.core.EntityTestable
import me.muksc.tacztweaks.feature.datapack.legacy.core.Target
import me.muksc.tacztweaks.feature.datapack.legacy.core.ValueRange
import net.minecraft.resources.ResourceLocation
import java.util.*
import kotlin.jvm.optionals.getOrNull

sealed class BulletSounds(
    val type: EBulletSoundsType,
    val target: List<Target>,
    val priority: Int
) {
    enum class EBulletSoundsType(
        override val key: String,
        override val codecProvider: () -> MapCodec<out BulletSounds>
    ) : DispatchCodec<BulletSounds> {
        BLOCK("block", { Block.CODEC }),
        ENTITY("entity", { Entity.CODEC }),
        CONSTANT("constant", { Constant.CODEC }),
        WHIZZ("whizz", { Whizz.CODEC }),
        AIRSPACE("airspace", { Airspace.CODEC });

        companion object {
            private val map = EBulletSoundsType.entries.associateBy(EBulletSoundsType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    open class Sound(
        val target: List<Target>,
        val sound: ResourceLocation,
        val volume: Float,
        val pitch: Float,
        range: Optional<Float>
    ) {
        val range: Float? = range.getOrNull()

        companion object {
            val CODEC: Codec<Sound> = RecordCodecBuilder.create { instance -> instance.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Sound::target),
                ResourceLocation.CODEC.fieldOf("sound").forGetter(Sound::sound),
                Codec.FLOAT.strictOptionalFieldOf("volume", 1.0F).forGetter(Sound::volume),
                Codec.FLOAT.strictOptionalFieldOf("pitch", 1.0F).forGetter(Sound::pitch),
                Codec.FLOAT.strictOptionalFieldOf("range").forGetter(Sound::range)
            ).apply(instance, ::Sound) }
        }
    }

    class DistanceSound(
        val threshold: Double,
        val sound: List<Sound>
    ) {
        companion object {
            val CODEC: Codec<DistanceSound> = RecordCodecBuilder.create { it.group(
                Codec.DOUBLE.fieldOf("threshold").forGetter(DistanceSound::threshold),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("sound", emptyList()).forGetter(DistanceSound::sound)
            ).apply(it, ::DistanceSound) }
        }
    }

    class Block(
        target: List<Target>,
        val blocks: List<BlockTestable>,
        val hit: List<BlockSound>,
        val pierce: List<BlockSound>,
        val `break`: List<BlockSound>,
        priority: Int
    ) : BulletSounds(EBulletSoundsType.BLOCK, target, priority) {
        class BlockSound(
            target: List<Target>,
            val blocks: List<BlockTestable>,
            sound: ResourceLocation,
            volume: Float,
            pitch: Float,
            range: Optional<Float>
        ) : Sound(target, sound, volume, pitch, range) {
            companion object {
                val CODEC: Codec<BlockSound> = RecordCodecBuilder.create { instance -> instance.group(
                    singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(BlockSound::target),
                    Codec.list(BlockTestable.CODEC).strictOptionalFieldOf("blocks", emptyList()).forGetter(BlockSound::blocks),
                    ResourceLocation.CODEC.fieldOf("sound").forGetter(BlockSound::sound),
                    Codec.FLOAT.strictOptionalFieldOf("volume", 1.0F).forGetter(BlockSound::volume),
                    Codec.FLOAT.strictOptionalFieldOf("pitch", 1.0F).forGetter(BlockSound::pitch),
                    Codec.FLOAT.strictOptionalFieldOf("range").forGetter(BlockSound::range)
                ).apply(instance, ::BlockSound) }
            }
        }

        companion object {
            val CODEC: MapCodec<Block> = RecordCodecBuilder.mapCodec { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Block::target),
                Codec.list(BlockTestable.CODEC).strictOptionalFieldOf("blocks", emptyList()).forGetter(Block::blocks),
                singleOrListCodec(BlockSound.CODEC).strictOptionalFieldOf("hit", emptyList()).forGetter(Block::hit),
                singleOrListCodec(BlockSound.CODEC).strictOptionalFieldOf("pierce", emptyList()).forGetter(Block::pierce),
                singleOrListCodec(BlockSound.CODEC).strictOptionalFieldOf("break", emptyList()).forGetter(Block::`break`),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Block::priority)
            ).apply(it, ::Block) }
        }
    }

    class Entity(
        target: List<Target>,
        val entities: List<EntityTestable>,
        val hit: List<EntitySound>,
        val pierce: List<EntitySound>,
        val kill: List<EntitySound>,
        priority: Int
    ) : BulletSounds(EBulletSoundsType.ENTITY, target, priority) {
        class EntitySound(
            target: List<Target>,
            val entities: List<EntityTestable>,
            sound: ResourceLocation,
            volume: Float,
            pitch: Float,
            range: Optional<Float>
        ) : Sound(target, sound, volume, pitch, range) {
            companion object {
                val CODEC: Codec<EntitySound> = RecordCodecBuilder.create { instance -> instance.group(
                    singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(EntitySound::target),
                    Codec.list(EntityTestable.CODEC).strictOptionalFieldOf("entities", emptyList()).forGetter(EntitySound::entities),
                    ResourceLocation.CODEC.fieldOf("sound").forGetter(EntitySound::sound),
                    Codec.FLOAT.strictOptionalFieldOf("volume", 1.0F).forGetter(EntitySound::volume),
                    Codec.FLOAT.strictOptionalFieldOf("pitch", 1.0F).forGetter(EntitySound::pitch),
                    Codec.FLOAT.strictOptionalFieldOf("range").forGetter(EntitySound::range)
                ).apply(instance, ::EntitySound) }
            }
        }

        companion object {
            val CODEC: MapCodec<Entity> = RecordCodecBuilder.mapCodec { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Entity::target),
                Codec.list(EntityTestable.CODEC).strictOptionalFieldOf("entities", emptyList()).forGetter(Entity::entities),
                singleOrListCodec(EntitySound.CODEC).strictOptionalFieldOf("hit", emptyList()).forGetter(Entity::hit),
                singleOrListCodec(EntitySound.CODEC).strictOptionalFieldOf("pierce", emptyList()).forGetter(Entity::pierce),
                singleOrListCodec(EntitySound.CODEC).strictOptionalFieldOf("kill", emptyList()).forGetter(Entity::kill),
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
            val CODEC: MapCodec<Constant> = RecordCodecBuilder.mapCodec { it.group(
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
            val CODEC: MapCodec<Whizz> = RecordCodecBuilder.mapCodec { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Whizz::target),
                Codec.list(DistanceSound.CODEC).sortedBy(DistanceSound::threshold).strictOptionalFieldOf("sounds", emptyList()).forGetter(Whizz::sounds),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Whizz::priority)
            ).apply(it, ::Whizz) }
        }
    }

    class Airspace(
        target: List<Target>,
        val airspace: ValueRange,
        val occlusion: ValueRange,
        val reflectivity: ValueRange,
        val sounds: List<DistanceSound>,
        priority: Int
    ) : BulletSounds(EBulletSoundsType.AIRSPACE, target, priority) {
        companion object {
            val CODEC: MapCodec<Airspace> = RecordCodecBuilder.mapCodec { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Airspace::target),
                ValueRange.CODEC.optionalFieldOf("airspace", ValueRange.DEFAULT).forGetter(Airspace::airspace),
                ValueRange.CODEC.optionalFieldOf("occlusion", ValueRange.DEFAULT).forGetter(Airspace::occlusion),
                ValueRange.CODEC.optionalFieldOf("reflectivity", ValueRange.DEFAULT).forGetter(Airspace::reflectivity),
                Codec.list(DistanceSound.CODEC).sortedBy(DistanceSound::threshold).strictOptionalFieldOf("sounds", emptyList()).forGetter(Airspace::sounds),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Airspace::priority)
            ).apply(it, ::Airspace) }
        }
    }

    companion object {
        val CODEC: Codec<BulletSounds> = EBulletSoundsType.CODEC.dispatch(BulletSounds::type, EBulletSoundsType::codec)
    }
}