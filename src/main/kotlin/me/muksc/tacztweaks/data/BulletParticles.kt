package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.DispatchCodec
import me.muksc.tacztweaks.singleOrListCodec
import net.minecraft.resources.ResourceLocation

sealed class BulletParticles(
    val type: EBulletParticlesType,
    val target: Target<*>
) {
    class Particle(
        val particle: String,
        val position: Coordinates,
        val delta: Coordinates,
        val speed: Double,
        val count: Int
    ) {
        sealed class Coordinates(
            val type: ECoordinatesType,
            val x: Double,
            val y: Double,
            val z: Double
        ) {
            enum class ECoordinatesType(
                override val key: String,
                override val codec: Codec<out Coordinates>
            ) : DispatchCodec<Coordinates> {
                ABSOLUTE("absolute", Absolute.CODEC),
                RELATIVE("relative", Relative.CODEC),
                LOCAL("local", Local.CODEC);

                companion object {
                    private val map = ECoordinatesType.entries.associateBy(ECoordinatesType::key)
                    val CODEC = DispatchCodec.getCodec(map::getValue)
                }
            }

            class Absolute(
                x: Double,
                y: Double,
                z: Double
            ) : Coordinates(ECoordinatesType.ABSOLUTE, x, y, z) {
                companion object {
                    val CODEC = RecordCodecBuilder.create<Absolute> { it.group(
                        Codec.DOUBLE.optionalFieldOf("x", 0.0).forGetter(Absolute::x),
                        Codec.DOUBLE.optionalFieldOf("y", 0.0).forGetter(Absolute::y),
                        Codec.DOUBLE.optionalFieldOf("z", 0.0).forGetter(Absolute::z)
                    ).apply(it, ::Absolute) }
                }
            }

            class Relative(
                x: Double,
                y: Double,
                z: Double
            ) : Coordinates(ECoordinatesType.RELATIVE, x, y, z) {
                companion object {
                    val CODEC = RecordCodecBuilder.create<Relative> { it.group(
                        Codec.DOUBLE.optionalFieldOf("x", 0.0).forGetter(Relative::x),
                        Codec.DOUBLE.optionalFieldOf("y", 0.0).forGetter(Relative::y),
                        Codec.DOUBLE.optionalFieldOf("z", 0.0).forGetter(Relative::z)
                    ).apply(it, ::Relative) }
                }
            }

            class Local(
                x: Double,
                y: Double,
                z: Double
            ) : Coordinates(ECoordinatesType.LOCAL, x, y, z) {
                companion object {
                    val CODEC = RecordCodecBuilder.create<Local> { it.group(
                        Codec.DOUBLE.optionalFieldOf("x", 0.0).forGetter(Local::x),
                        Codec.DOUBLE.optionalFieldOf("y", 0.0).forGetter(Local::y),
                        Codec.DOUBLE.optionalFieldOf("z", 0.0).forGetter(Local::z)
                    ).apply(it, ::Local) }
                }
            }

            companion object {
                val CODEC = ECoordinatesType.CODEC.dispatch(Coordinates::type, ECoordinatesType::codec)
            }
        }

        companion object {
            val CODEC = RecordCodecBuilder.create<Particle> { it.group(
                Codec.STRING.fieldOf("particle").forGetter(Particle::particle),
                Coordinates.CODEC.optionalFieldOf("position", Coordinates.Relative(0.0, 0.0, 0.0)).forGetter(Particle::position),
                Coordinates.CODEC.optionalFieldOf("delta", Coordinates.Absolute(0.0, 0.0, 0.0)).forGetter(Particle::delta),
                Codec.DOUBLE.optionalFieldOf("speed", 0.0).forGetter(Particle::speed),
                Codec.INT.optionalFieldOf("count", 1).forGetter(Particle::count)
            ).apply(it, ::Particle) }
        }
    }

    enum class EBulletParticlesType(
        override val key: String,
        override val codec: Codec<out BulletParticles>
    ) : DispatchCodec<BulletParticles> {
        BLOCK("block", Block.CODEC),
        ENTITY("entity", Entity.CODEC);

        companion object {
            private val map = EBulletParticlesType.entries.associateBy(EBulletParticlesType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    class Block(
        target: Target<*>,
        val blocks: List<BlockOrBlockTag>,
        val hit: List<Particle>,
        val pierce: List<Particle>,
        val `break`: List<Particle>
    ) : BulletParticles(EBulletParticlesType.BLOCK, target) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Block> { it.group(
                Target.CODEC.optionalFieldOf("target", Target.Fallback).forGetter(Block::target),
                Codec.list(BlockOrBlockTag.CODEC).optionalFieldOf("blocks", emptyList()).forGetter(Block::blocks),
                singleOrListCodec(Particle.CODEC).optionalFieldOf("hit", emptyList()).forGetter(Block::hit),
                singleOrListCodec(Particle.CODEC).optionalFieldOf("pierce", emptyList()).forGetter(Block::pierce),
                singleOrListCodec(Particle.CODEC).optionalFieldOf("break", emptyList()).forGetter(Block::`break`)
            ).apply(it, ::Block) }
        }
    }

    class Entity(
        target: Target<*>,
        val entities: List<EntityOrEntityTag>,
        val hit: List<Particle>,
        val pierce: List<Particle>,
        val kill: List<Particle>
    ) : BulletParticles(EBulletParticlesType.BLOCK, target) {
        companion object {
            val CODEC = RecordCodecBuilder.create<Entity> { it.group(
                Target.CODEC.optionalFieldOf("target", Target.Fallback).forGetter(Entity::target),
                Codec.list(EntityOrEntityTag.CODEC).optionalFieldOf("entities", emptyList()).forGetter(Entity::entities),
                singleOrListCodec(Particle.CODEC).optionalFieldOf("hit", emptyList()).forGetter(Entity::hit),
                singleOrListCodec(Particle.CODEC).optionalFieldOf("pierce", emptyList()).forGetter(Entity::pierce),
                singleOrListCodec(Particle.CODEC).optionalFieldOf("kill", emptyList()).forGetter(Entity::kill)
            ).apply(it, ::Entity) }
        }
    }

    companion object {
        val CODEC = EBulletParticlesType.CODEC.dispatch(BulletParticles::type, EBulletParticlesType::codec)
    }
}