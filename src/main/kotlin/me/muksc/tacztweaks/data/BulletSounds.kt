package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import java.util.*

@Suppress("UNCHECKED_CAST")
fun <T> Codec<T>.nullableFieldOf(name: String): MapCodec<T?> =
    Codec.optionalField<T>(name, this).xmap(
        { it.orElse(null) },
        { Optional.ofNullable(it) as Optional<T> }
    )

class BulletSounds(
    val target: Target,
    val blocks: List<BlockSounds>,
    val entities: List<EntitySounds>,
    val whizzes: List<Whizz>
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

    class BlockSounds(
        val blocks: List<BlockOrBlockTag>,
        val hit: Sound?,
        val pierce: Sound?,
        val `break`: Sound?
    ) {
        companion object {
            val CODEC = RecordCodecBuilder.create<BlockSounds> { it.group(
                Codec.list(BlockOrBlockTag.CODEC).optionalFieldOf("blocks", emptyList()).forGetter(BlockSounds::blocks),
                Sound.CODEC.nullableFieldOf("hit").forGetter(BlockSounds::hit),
                Sound.CODEC.nullableFieldOf("pierce").forGetter(BlockSounds::pierce),
                Sound.CODEC.nullableFieldOf("break").forGetter(BlockSounds::`break`)
            ).apply(it, ::BlockSounds) }
        }
    }

    class EntitySounds(
        val entities: List<EntityOrEntityTag>,
        val hit: String?,
        val pierce: String?,
        val kill: String?
    ) {
        companion object {
            val CODEC = RecordCodecBuilder.create<EntitySounds> { it.group(
                Codec.list(EntityOrEntityTag.CODEC).fieldOf("entities").forGetter(EntitySounds::entities),
                Codec.STRING.nullableFieldOf("hit").forGetter(EntitySounds::hit),
                Codec.STRING.nullableFieldOf("pierce").forGetter(EntitySounds::pierce),
                Codec.STRING.nullableFieldOf("kill").forGetter(EntitySounds::kill)
            ).apply(it, ::EntitySounds) }
        }
    }

    class Whizz(
        val threshold: Double,
        val sound: Sound
    ) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Whizz> { it.group(
                Codec.DOUBLE.fieldOf("threshold").forGetter(Whizz::threshold),
                Sound.CODEC.fieldOf("sound").forGetter(Whizz::sound)
            ).apply(it, ::Whizz) }
        }
    }

    companion object {
        val CODEC = RecordCodecBuilder.create<BulletSounds> { it.group(
            Target.CODEC.optionalFieldOf("target", Target.DEFAULT).forGetter(BulletSounds::target),
            Codec.list(BlockSounds.CODEC).optionalFieldOf("blocks", emptyList()).forGetter(BulletSounds::blocks),
            Codec.list(EntitySounds.CODEC).optionalFieldOf("entities", emptyList()).forGetter(BulletSounds::entities),
            Codec.list(Whizz.CODEC).optionalFieldOf("whizzes", emptyList()).forGetter(BulletSounds::whizzes)
        ).apply(it, ::BulletSounds) }
    }
}