package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.data.codec.DispatchCodec
import me.muksc.tacztweaks.data.codec.singleOrListCodec
import me.muksc.tacztweaks.data.codec.strictOptionalFieldOf
import me.muksc.tacztweaks.data.core.BlockTestable
import me.muksc.tacztweaks.data.core.EntityTestable
import me.muksc.tacztweaks.data.core.Target

sealed class BulletParticles(
    val type: EBulletParticlesType,
    val target: List<Target>,
    val priority: Int
) {
    class Particle(
        val particle: String,
        val position: Coordinates,
        val delta: Coordinates,
        val speed: Double,
        val count: Int,
        val force: Boolean,
        val duration: Int
    ) {
        sealed class Coordinates(
            val type: ECoordinatesType,
            val x: Double,
            val y: Double,
            val z: Double
        ) {
            enum class ECoordinatesType(
                override val key: String,
                override val codecProvider: () -> Codec<out Coordinates>
            ) : DispatchCodec<Coordinates> {
                ABSOLUTE("absolute", { Absolute.CODEC }),
                RELATIVE("relative", { Relative.CODEC }),
                LOCAL("local", { Local.CODEC });

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
                    val CODEC: Codec<Absolute> = RecordCodecBuilder.create<Absolute> { it.group(
                        Codec.DOUBLE.strictOptionalFieldOf("x", 0.0).forGetter(Absolute::x),
                        Codec.DOUBLE.strictOptionalFieldOf("y", 0.0).forGetter(Absolute::y),
                        Codec.DOUBLE.strictOptionalFieldOf("z", 0.0).forGetter(Absolute::z)
                    ).apply(it, ::Absolute) }
                }
            }

            class Relative(
                x: Double,
                y: Double,
                z: Double
            ) : Coordinates(ECoordinatesType.RELATIVE, x, y, z) {
                companion object {
                    val CODEC: Codec<Relative> = RecordCodecBuilder.create<Relative> { it.group(
                        Codec.DOUBLE.strictOptionalFieldOf("x", 0.0).forGetter(Relative::x),
                        Codec.DOUBLE.strictOptionalFieldOf("y", 0.0).forGetter(Relative::y),
                        Codec.DOUBLE.strictOptionalFieldOf("z", 0.0).forGetter(Relative::z)
                    ).apply(it, ::Relative) }
                }
            }

            class Local(
                x: Double,
                y: Double,
                z: Double
            ) : Coordinates(ECoordinatesType.LOCAL, x, y, z) {
                companion object {
                    val CODEC: Codec<Local> = RecordCodecBuilder.create<Local> { it.group(
                        Codec.DOUBLE.strictOptionalFieldOf("x", 0.0).forGetter(Local::x),
                        Codec.DOUBLE.strictOptionalFieldOf("y", 0.0).forGetter(Local::y),
                        Codec.DOUBLE.strictOptionalFieldOf("z", 0.0).forGetter(Local::z)
                    ).apply(it, ::Local) }
                }
            }

            companion object {
                val CODEC: Codec<Coordinates> = ECoordinatesType.CODEC.dispatch(Coordinates::type) { it.codecProvider() }
            }
        }

        companion object {
            val CODEC: Codec<Particle> = RecordCodecBuilder.create<Particle> { it.group(
                Codec.STRING.fieldOf("particle").forGetter(Particle::particle),
                Coordinates.CODEC.strictOptionalFieldOf("position", Coordinates.Relative(0.0, 0.0, 0.0)).forGetter(Particle::position),
                Coordinates.CODEC.strictOptionalFieldOf("delta", Coordinates.Absolute(0.0, 0.0, 0.0)).forGetter(Particle::delta),
                Codec.DOUBLE.strictOptionalFieldOf("speed", 0.0).forGetter(Particle::speed),
                Codec.INT.strictOptionalFieldOf("count", 1).forGetter(Particle::count),
                Codec.BOOL.strictOptionalFieldOf("force", false).forGetter(Particle::force),
                Codec.INT.strictOptionalFieldOf("duration", 1).forGetter(Particle::duration)
            ).apply(it, ::Particle) }
        }
    }

    enum class EBulletParticlesType(
        override val key: String,
        override val codecProvider: () -> Codec<out BulletParticles>
    ) : DispatchCodec<BulletParticles> {
        BLOCK("block", { Block.CODEC }),
        ENTITY("entity", { Entity.CODEC });

        companion object {
            private val map = EBulletParticlesType.entries.associateBy(EBulletParticlesType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    class Block(
        target: List<Target>,
        val blocks: List<BlockTestable>,
        val hit: List<Particle>,
        val pierce: List<Particle>,
        val `break`: List<Particle>,
        priority: Int
    ) : BulletParticles(EBulletParticlesType.BLOCK, target, priority) {
        companion object {
            val CODEC: Codec<Block> = RecordCodecBuilder.create<Block> { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Block::target),
                Codec.list(BlockTestable.CODEC).strictOptionalFieldOf("blocks", emptyList()).forGetter(Block::blocks),
                singleOrListCodec(Particle.CODEC).strictOptionalFieldOf("hit", emptyList()).forGetter(Block::hit),
                singleOrListCodec(Particle.CODEC).strictOptionalFieldOf("pierce", emptyList()).forGetter(Block::pierce),
                singleOrListCodec(Particle.CODEC).strictOptionalFieldOf("break", emptyList()).forGetter(Block::`break`),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Block::priority)
            ).apply(it, ::Block) }
        }
    }

    class Entity(
        target: List<Target>,
        val entities: List<EntityTestable>,
        val hit: List<Particle>,
        val pierce: List<Particle>,
        val kill: List<Particle>,
        priority: Int
    ) : BulletParticles(EBulletParticlesType.ENTITY, target, priority) {
        companion object {
            val CODEC: Codec<Entity> = RecordCodecBuilder.create<Entity> { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Entity::target),
                Codec.list(EntityTestable.CODEC).strictOptionalFieldOf("entities", emptyList()).forGetter(Entity::entities),
                singleOrListCodec(Particle.CODEC).strictOptionalFieldOf("hit", emptyList()).forGetter(Entity::hit),
                singleOrListCodec(Particle.CODEC).strictOptionalFieldOf("pierce", emptyList()).forGetter(Entity::pierce),
                singleOrListCodec(Particle.CODEC).strictOptionalFieldOf("kill", emptyList()).forGetter(Entity::kill),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Entity::priority)
            ).apply(it, ::Entity) }
        }
    }

    companion object {
        val CODEC: Codec<BulletParticles> = EBulletParticlesType.CODEC.dispatch(BulletParticles::type) { it.codecProvider() }
    }
}