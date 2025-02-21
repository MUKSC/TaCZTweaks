package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.DispatchCodec
import net.minecraft.resources.ResourceLocation
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

sealed class BulletSounds(
    val type: EBulletSoundsType,
    val target: Target<*>
) {
    class Sound(
        val sound: ResourceLocation,
        val volume: Float,
        val pitch: Float
    ) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Sound> { it.group(
                ResourceLocation.CODEC.fieldOf("sound").forGetter(Sound::sound),
                Codec.FLOAT.optionalFieldOf("volume", 1.0F).forGetter(Sound::volume),
                Codec.FLOAT.optionalFieldOf("pitch", 1.0F).forGetter(Sound::pitch),
            ).apply(it, ::Sound) }
        }
    }

    enum class EBulletSoundsType(
        override val key: String,
        override val codec: Codec<out BulletSounds>
    ) : DispatchCodec<BulletSounds> {
        BLOCK("block", Block.CODEC),
        ENTITY("entity", Entity.CODEC),
        WHIZZ("whizz", Whizz.CODEC);

        companion object {
            private val map = EBulletSoundsType.entries.associateBy(EBulletSoundsType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    class Block(
        target: Target<*>,
        val blocks: List<BlockOrBlockTag>,
        val hit: Sound?,
        val pierce: Sound?,
        val `break`: Sound?
    ) : BulletSounds(EBulletSoundsType.BLOCK, target) {
        constructor(
            target: Target<*>,
            blocks: List<BlockOrBlockTag>,
            hit: Optional<Sound>,
            pierce: Optional<Sound>,
            `break`: Optional<Sound>
        ) : this(target, blocks, hit.getOrNull(), pierce.getOrNull(), `break`.getOrNull())

        companion object {
            val CODEC = RecordCodecBuilder.create<Block> { it.group(
                Target.CODEC.optionalFieldOf("target", Target.Fallback).forGetter(Block::target),
                Codec.list(BlockOrBlockTag.CODEC).optionalFieldOf("blocks", emptyList()).forGetter(Block::blocks),
                Sound.CODEC.optionalFieldOf("hit").forGetter { Optional.ofNullable(it.hit) },
                Sound.CODEC.optionalFieldOf("pierce").forGetter { Optional.ofNullable(it.pierce) },
                Sound.CODEC.optionalFieldOf("break").forGetter { Optional.ofNullable(it.`break`) }
            ).apply(it, ::Block) }
        }
    }

    class Entity(
        target: Target<*>,
        val entities: List<EntityOrEntityTag>,
        val hit: Sound?,
        val pierce: Sound?,
        val kill: Sound?
    ) : BulletSounds(EBulletSoundsType.ENTITY, target) {
        constructor(
            target: Target<*>,
            entities: List<EntityOrEntityTag>,
            hit: Optional<Sound>,
            pierce: Optional<Sound>,
            kill: Optional<Sound>
        ) : this(target, entities, hit.getOrNull(), pierce.getOrNull(), kill.getOrNull())

        companion object {
            val CODEC = RecordCodecBuilder.create<Entity> { it.group(
                Target.CODEC.optionalFieldOf("target", Target.Fallback).forGetter(Entity::target),
                Codec.list(EntityOrEntityTag.CODEC).fieldOf("entities").forGetter(Entity::entities),
                Sound.CODEC.optionalFieldOf("hit").forGetter { Optional.ofNullable(it.hit) },
                Sound.CODEC.optionalFieldOf("pierce").forGetter { Optional.ofNullable(it.pierce) },
                Sound.CODEC.optionalFieldOf("kill").forGetter { Optional.ofNullable(it.kill) }
            ).apply(it, ::Entity) }
        }
    }

    class Whizz(
        target: Target<*>,
        val sounds: List<DistanceSound>
    ) : BulletSounds(EBulletSoundsType.WHIZZ, target) {
        class DistanceSound(
            val threshold: Double,
            val sound: Sound
        ) {
            companion object {
                val CODEC = RecordCodecBuilder.create<DistanceSound> { it.group(
                    Codec.DOUBLE.fieldOf("threshold").forGetter(DistanceSound::threshold),
                    Sound.CODEC.fieldOf("sound").forGetter(DistanceSound::sound)
                ).apply(it, ::DistanceSound) }
            }
        }

        companion object {
            val CODEC = RecordCodecBuilder.create<Whizz> { it.group(
                Target.CODEC.optionalFieldOf("target", Target.Fallback).forGetter(Whizz::target),
                Codec.list(DistanceSound.CODEC).optionalFieldOf("sounds", emptyList()).forGetter(Whizz::sounds)
            ).apply(it, ::Whizz) }
        }
    }

    companion object {
        val CODEC = EBulletSoundsType.CODEC.dispatch(BulletSounds::type, EBulletSoundsType::codec)
    }
}