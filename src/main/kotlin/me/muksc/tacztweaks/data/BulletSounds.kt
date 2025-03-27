package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.DispatchCodec
import me.muksc.tacztweaks.singleOrListCodec
import net.minecraft.resources.ResourceLocation

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
        val hit: List<Sound>,
        val pierce: List<Sound>,
        val `break`: List<Sound>
    ) : BulletSounds(EBulletSoundsType.BLOCK, target) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Block> { it.group(
                Target.CODEC.optionalFieldOf("target", Target.Fallback).forGetter(Block::target),
                Codec.list(BlockOrBlockTag.CODEC).optionalFieldOf("blocks", emptyList()).forGetter(Block::blocks),
                singleOrListCodec(Sound.CODEC).optionalFieldOf("hit", emptyList()).forGetter(Block::hit),
                singleOrListCodec(Sound.CODEC).optionalFieldOf("pierce", emptyList()).forGetter(Block::pierce),
                singleOrListCodec(Sound.CODEC).optionalFieldOf("break", emptyList()).forGetter(Block::`break`)
            ).apply(it, ::Block) }
        }
    }

    class Entity(
        target: Target<*>,
        val entities: List<EntityOrEntityTag>,
        val hit: List<Sound>,
        val pierce: List<Sound>,
        val kill: List<Sound>
    ) : BulletSounds(EBulletSoundsType.ENTITY, target) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Entity> { it.group(
                Target.CODEC.optionalFieldOf("target", Target.Fallback).forGetter(Entity::target),
                Codec.list(EntityOrEntityTag.CODEC).fieldOf("entities").forGetter(Entity::entities),
                singleOrListCodec(Sound.CODEC).optionalFieldOf("hit", emptyList()).forGetter(Entity::hit),
                singleOrListCodec(Sound.CODEC).optionalFieldOf("pierce", emptyList()).forGetter(Entity::pierce),
                singleOrListCodec(Sound.CODEC).optionalFieldOf("kill", emptyList()).forGetter(Entity::kill)
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