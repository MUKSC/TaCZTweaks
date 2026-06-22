package me.muksc.tacztweaks.feature.datapack.legacy

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.muksc.tacztweaks.core.codec.DispatchCodec
import me.muksc.tacztweaks.core.codec.singleOrListCodec
import me.muksc.tacztweaks.core.codec.strictOptionalFieldOf
import me.muksc.tacztweaks.feature.datapack.legacy.core.BlockTestable
import me.muksc.tacztweaks.feature.datapack.legacy.core.EntityTestable
import me.muksc.tacztweaks.feature.datapack.legacy.core.Target

sealed class BulletParticles(
    val type: EBulletParticlesType,
    val target: List<Target>,
    val priority: Int
) {
    enum class EBulletParticlesType(
        override val key: String,
        override val codecProvider: () -> MapCodec<out BulletParticles>
    ) : DispatchCodec<BulletParticles> {
        BLOCK("block", { Block.CODEC }),
        ENTITY("entity", { Entity.CODEC });

        companion object {
            private val map = entries.associateBy(EBulletParticlesType::key)
            val CODEC = DispatchCodec.getCodec(map::getValue)
        }
    }

    open class Particle(
        val target: List<Target>,
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
                override val codecProvider: () -> MapCodec<out Coordinates>
            ) : DispatchCodec<Coordinates> {
                ABSOLUTE("absolute", { Absolute.CODEC }),
                RELATIVE("relative", { Relative.CODEC }),
                LOCAL("local", { Local.CODEC });

                companion object {
                    private val map = entries.associateBy(ECoordinatesType::key)
                    val CODEC = DispatchCodec.getCodec(map::getValue)
                }
            }

            class Absolute(
                x: Double,
                y: Double,
                z: Double
            ) : Coordinates(ECoordinatesType.ABSOLUTE, x, y, z) {
                companion object {
                    val CODEC: MapCodec<Absolute> = RecordCodecBuilder.mapCodec { it.group(
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
                    val CODEC: MapCodec<Relative> = RecordCodecBuilder.mapCodec { it.group(
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
                    val CODEC: MapCodec<Local> = RecordCodecBuilder.mapCodec { it.group(
                        Codec.DOUBLE.strictOptionalFieldOf("x", 0.0).forGetter(Local::x),
                        Codec.DOUBLE.strictOptionalFieldOf("y", 0.0).forGetter(Local::y),
                        Codec.DOUBLE.strictOptionalFieldOf("z", 0.0).forGetter(Local::z)
                    ).apply(it, ::Local) }
                }
            }

            companion object {
                val CODEC: Codec<Coordinates> = ECoordinatesType.CODEC.dispatch(Coordinates::type, ECoordinatesType::codec)
            }
        }

        companion object {
            val CODEC: MapCodec<Particle> = RecordCodecBuilder.mapCodec { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Particle::target),
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

    class Block(
        target: List<Target>,
        val blocks: List<BlockTestable>,
        val hit: List<BlockParticle>,
        val pierce: List<BlockParticle>,
        val `break`: List<BlockParticle>,
        priority: Int
    ) : BulletParticles(EBulletParticlesType.BLOCK, target, priority) {
        class BlockParticle(
            target: List<Target>,
            val blocks: List<BlockTestable>,
            particle: String,
            position: Coordinates,
            delta: Coordinates,
            speed: Double,
            count: Int,
            force: Boolean,
            duration: Int
        ) : Particle(target, particle, position, delta, speed, count, force, duration) {
            companion object {
                val CODEC: Codec<BlockParticle> = RecordCodecBuilder.create { it.group(
                    singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(BlockParticle::target),
                    Codec.list(BlockTestable.CODEC).strictOptionalFieldOf("blocks", emptyList()).forGetter(BlockParticle::blocks),
                    Codec.STRING.fieldOf("particle").forGetter(BlockParticle::particle),
                    Coordinates.CODEC.strictOptionalFieldOf("position", Coordinates.Relative(0.0, 0.0, 0.0)).forGetter(BlockParticle::position),
                    Coordinates.CODEC.strictOptionalFieldOf("delta", Coordinates.Absolute(0.0, 0.0, 0.0)).forGetter(BlockParticle::delta),
                    Codec.DOUBLE.strictOptionalFieldOf("speed", 0.0).forGetter(BlockParticle::speed),
                    Codec.INT.strictOptionalFieldOf("count", 1).forGetter(BlockParticle::count),
                    Codec.BOOL.strictOptionalFieldOf("force", false).forGetter(BlockParticle::force),
                    Codec.INT.strictOptionalFieldOf("duration", 1).forGetter(BlockParticle::duration)
                ).apply(it, ::BlockParticle) }
            }
        }

        companion object {
            val CODEC: MapCodec<Block> = RecordCodecBuilder.mapCodec { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Block::target),
                Codec.list(BlockTestable.CODEC).strictOptionalFieldOf("blocks", emptyList()).forGetter(Block::blocks),
                singleOrListCodec(BlockParticle.CODEC).strictOptionalFieldOf("hit", emptyList()).forGetter(Block::hit),
                singleOrListCodec(BlockParticle.CODEC).strictOptionalFieldOf("pierce", emptyList()).forGetter(Block::pierce),
                singleOrListCodec(BlockParticle.CODEC).strictOptionalFieldOf("break", emptyList()).forGetter(Block::`break`),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Block::priority)
            ).apply(it, ::Block) }
        }
    }

    class Entity(
        target: List<Target>,
        val entities: List<EntityTestable>,
        val hit: List<EntityParticle>,
        val pierce: List<EntityParticle>,
        val kill: List<EntityParticle>,
        priority: Int
    ) : BulletParticles(EBulletParticlesType.ENTITY, target, priority) {
        class EntityParticle(
            target: List<Target>,
            val entities: List<EntityTestable>,
            particle: String,
            position: Coordinates,
            delta: Coordinates,
            speed: Double,
            count: Int,
            force: Boolean,
            duration: Int
        ) : Particle(target, particle, position, delta, speed, count, force, duration) {
            companion object {
                val CODEC: Codec<EntityParticle> = RecordCodecBuilder.create { it.group(
                    singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(EntityParticle::target),
                    Codec.list(EntityTestable.CODEC).strictOptionalFieldOf("entities", emptyList()).forGetter(EntityParticle::entities),
                    Codec.STRING.fieldOf("particle").forGetter(EntityParticle::particle),
                    Coordinates.CODEC.strictOptionalFieldOf("position", Coordinates.Relative(0.0, 0.0, 0.0)).forGetter(EntityParticle::position),
                    Coordinates.CODEC.strictOptionalFieldOf("delta", Coordinates.Absolute(0.0, 0.0, 0.0)).forGetter(EntityParticle::delta),
                    Codec.DOUBLE.strictOptionalFieldOf("speed", 0.0).forGetter(EntityParticle::speed),
                    Codec.INT.strictOptionalFieldOf("count", 1).forGetter(EntityParticle::count),
                    Codec.BOOL.strictOptionalFieldOf("force", false).forGetter(EntityParticle::force),
                    Codec.INT.strictOptionalFieldOf("duration", 1).forGetter(EntityParticle::duration)
                ).apply(it, ::EntityParticle) }
            }
        }

        companion object {
            val CODEC: MapCodec<Entity> = RecordCodecBuilder.mapCodec { it.group(
                singleOrListCodec(Target.CODEC).strictOptionalFieldOf("target", emptyList()).forGetter(Entity::target),
                Codec.list(EntityTestable.CODEC).strictOptionalFieldOf("entities", emptyList()).forGetter(Entity::entities),
                singleOrListCodec(EntityParticle.CODEC).strictOptionalFieldOf("hit", emptyList()).forGetter(Entity::hit),
                singleOrListCodec(EntityParticle.CODEC).strictOptionalFieldOf("pierce", emptyList()).forGetter(Entity::pierce),
                singleOrListCodec(EntityParticle.CODEC).strictOptionalFieldOf("kill", emptyList()).forGetter(Entity::kill),
                Codec.INT.strictOptionalFieldOf("priority", 0).forGetter(Entity::priority)
            ).apply(it, ::Entity) }
        }
    }

    companion object {
        val CODEC: Codec<BulletParticles> = EBulletParticlesType.CODEC.dispatch(BulletParticles::type, EBulletParticlesType::codec)
    }
}