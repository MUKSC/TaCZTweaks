package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.DispatchCodec
import me.muksc.tacztweaks.singleOrListCodec
import me.muksc.tacztweaks.strictOptionalFieldOf
import net.minecraft.resources.ResourceLocation

sealed class BulletSounds(
    val type: EBulletSoundsType,
    val target: List<Target>
) {
    class Sound(
        val sound: ResourceLocation,
        val volume: Float,
        val pitch: Float
    ) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Sound> { it.group(
                ResourceLocation.CODEC.fieldOf("sound").forGetter(Sound::sound),
                Codec.FLOAT.strictOptionalFieldOf("volume", 1.0F).forGetter(Sound::volume),
                Codec.FLOAT.strictOptionalFieldOf("pitch", 1.0F).forGetter(Sound::pitch),
            ).apply(it, ::Sound) }
        }
    }

    enum class EBulletSoundsType(
        override val key: String,
        override val codecProvider: () -> Codec<out BulletSounds>
    ) : DispatchCodec<BulletSounds> {
        BLOCK("block", { Block.CODEC }),
        ENTITY("entity", { Entity.CODEC }),
        CONSTANT("constant", { Constant.CODEC }),
        WHIZZ("whizz", { Whizz.CODEC });

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
        val `break`: List<Sound>
    ) : BulletSounds(EBulletSoundsType.BLOCK, target) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Block> { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Block::target),
                Codec.list(BlockTestable.CODEC).strictOptionalFieldOf("blocks", emptyList()).forGetter(Block::blocks),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("hit", emptyList()).forGetter(Block::hit),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("pierce", emptyList()).forGetter(Block::pierce),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("break", emptyList()).forGetter(Block::`break`)
            ).apply(it, ::Block) }
        }
    }

    class Entity(
        target: List<Target>,
        val entities: List<EntityTestable>,
        val hit: List<Sound>,
        val pierce: List<Sound>,
        val kill: List<Sound>
    ) : BulletSounds(EBulletSoundsType.ENTITY, target) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Entity> { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Entity::target),
                Codec.list(EntityTestable.CODEC).fieldOf("entities").forGetter(Entity::entities),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("hit", emptyList()).forGetter(Entity::hit),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("pierce", emptyList()).forGetter(Entity::pierce),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("kill", emptyList()).forGetter(Entity::kill)
            ).apply(it, ::Entity) }
        }
    }

    class Constant(
        target: List<Target>,
        val interval: Int,
        val sounds: List<Sound>
    ) : BulletSounds(EBulletSoundsType.CONSTANT, target) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Constant> { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Constant::target),
                Codec.INT.fieldOf("interval").forGetter(Constant::interval),
                singleOrListCodec(Sound.CODEC).strictOptionalFieldOf("sounds", emptyList()).forGetter(Constant::sounds)
            ).apply(it, ::Constant) }
        }
    }

    class Whizz(
        target: List<Target>,
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
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Whizz::target),
                Codec.list(DistanceSound.CODEC).strictOptionalFieldOf("sounds", emptyList()).forGetter(Whizz::sounds)
            ).apply(it, ::Whizz) }
        }
    }

    companion object {
        val CODEC = EBulletSoundsType.CODEC.dispatch(BulletSounds::type) { it.codecProvider() }
    }
}