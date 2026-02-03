package me.muksc.tacztweaks.data.manager

import com.google.gson.JsonElement
import com.mojang.brigadier.StringReader
import com.mojang.serialization.JsonOps
import com.tacz.guns.entity.EntityKineticBullet
import me.muksc.tacztweaks.anyOrEmpty
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.data.BulletParticles
import me.muksc.tacztweaks.id
import me.muksc.tacztweaks.mixininterface.features.EntityKineticBulletExtension
import me.muksc.tacztweaks.thenPrioritizeBy
import net.minecraft.commands.arguments.ParticleArgument
import net.minecraft.commands.arguments.coordinates.LocalCoordinates
import net.minecraft.commands.arguments.coordinates.WorldCoordinate
import net.minecraft.commands.arguments.coordinates.WorldCoordinates
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

private val COMPARATOR = compareBy<BulletParticles> { it.priority }
    .thenPrioritizeBy { it.target.isNotEmpty() }
    .thenPrioritizeBy { when (it) {
        is BulletParticles.Block -> it.blocks.isNotEmpty()
        is BulletParticles.Entity -> it.entities.isNotEmpty()
    } }

object BulletParticlesManager : BaseDataManager<BulletParticles>("bullet_particles", COMPARATOR) {
    private val emitters: MutableList<ParticleEmitter> = mutableListOf()

    override fun debugEnabled(): Boolean = Config.Debug.bulletParticles()

    override fun parseElement(json: JsonElement): BulletParticles =
        BulletParticles.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false) { /* Nothing */ }

    private inline fun <reified T : BulletParticles, E> getParticle(
        entity: EntityKineticBullet,
        location: Vec3,
        selector: (T) -> List<E>,
        predicate: (E) -> Boolean
    ): Pair<ResourceLocation, T>? = byType<T>().entries.firstOrNull { (_, particles) ->
        particles.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(location)) }
                && selector(particles).anyOrEmpty(predicate)
    }?.toPair()

    fun onLevelTick(level: ServerLevel) {
        val iterator = emitters.iterator()
        while (iterator.hasNext()) {
            val emitter = iterator.next()
            emitter.remainingDuration -= 1
            for (player in level.players()) {
                level.sendParticles(
                    player,
                    emitter.options,
                    emitter.particle.force,
                    emitter.coordinates.x,
                    emitter.coordinates.y,
                    emitter.coordinates.z,
                    emitter.particle.count,
                    emitter.deltaCoordinates.x,
                    emitter.deltaCoordinates.y,
                    emitter.deltaCoordinates.z,
                    emitter.particle.speed
                )
            }
            if (emitter.remainingDuration <= 0) iterator.remove()
        }
    }

    fun handleBlockParticle(type: EBlockParticleType, level: ServerLevel, entity: EntityKineticBullet, result: BlockHitResult, state: BlockState) {
        val (id, particles) = getParticle(entity, result.location, BulletParticles.Block::blocks) {
            it.test(level, result.blockPos, state)
        } ?: return
        logDebug { "Using block bullet particles: $id" }
        for (particle in type.getParticle(particles)) {
            if (!particle.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(result.location)) }) continue
            if (!particle.blocks.anyOrEmpty { it.test(level, result.blockPos, state) }) continue
            val id = state.block.id?.toString()
            particle.summon(level.server, entity, id)
        }
    }

    fun handleEntityParticle(type: EEntityParticleType, level: ServerLevel, entity: EntityKineticBullet, location: Vec3, target: Entity) {
        val (id, particles) = getParticle(entity, location, BulletParticles.Entity::entities) {
            it.test(target)
        } ?: return
        logDebug { "Using entity bullet particles: $id" }
        for (particle in type.getParticle(particles)) {
            if (!particle.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(location)) }) continue
            if (!particle.entities.anyOrEmpty { it.test(target) }) continue
            particle.summon(level.server, entity)
        }
    }

    private fun BulletParticles.Particle.summon(server: MinecraftServer, entity: EntityKineticBullet, context: String? = null) {
        val ext = entity as EntityKineticBulletExtension
        val source = entity.createCommandSourceStack()
            .withPosition(ext.`tacztweaks$getPosition`())
        val particles = server.registryAccess().lookupOrThrow(Registries.PARTICLE_TYPE)
        val reader = StringReader(if (context != null) particle.format(context) else particle)
        val particleOptions = ParticleArgument.readParticle(reader, particles)
        val coordinates = when (position.type) {
            BulletParticles.Particle.Coordinates.ECoordinatesType.ABSOLUTE -> WorldCoordinates(
                WorldCoordinate(false, position.x),
                WorldCoordinate(false, position.y),
                WorldCoordinate(false, position.z)
            )
            BulletParticles.Particle.Coordinates.ECoordinatesType.RELATIVE -> WorldCoordinates(
                WorldCoordinate(true, position.x),
                WorldCoordinate(true, position.y),
                WorldCoordinate(true, position.z)
            )
            BulletParticles.Particle.Coordinates.ECoordinatesType.LOCAL -> LocalCoordinates(
                position.x,
                position.y,
                position.z
            )
        }.getPosition(source)
        val deltaCoordinates = when (delta.type) {
            BulletParticles.Particle.Coordinates.ECoordinatesType.ABSOLUTE -> WorldCoordinates(
                WorldCoordinate(false, delta.x),
                WorldCoordinate(false, delta.y),
                WorldCoordinate(false, delta.z)
            )
            BulletParticles.Particle.Coordinates.ECoordinatesType.RELATIVE -> WorldCoordinates(
                WorldCoordinate(true, delta.x),
                WorldCoordinate(true, delta.y),
                WorldCoordinate(true, delta.z)
            )
            BulletParticles.Particle.Coordinates.ECoordinatesType.LOCAL -> LocalCoordinates(
                delta.x,
                delta.y,
                delta.z
            )
        }.getPosition(source)
        emitters.add(ParticleEmitter(
            this,
            particleOptions,
            coordinates,
            deltaCoordinates,
            duration
        ))
    }

    enum class EBlockParticleType(val getParticle: (BulletParticles.Block) -> List<BulletParticles.Block.BlockParticle>) {
        HIT(BulletParticles.Block::hit),
        PIERCE(BulletParticles.Block::pierce),
        BREAK(BulletParticles.Block::`break`)
    }

    enum class EEntityParticleType(val getParticle: (BulletParticles.Entity) -> List<BulletParticles.Entity.EntityParticle>) {
        HIT(BulletParticles.Entity::hit),
        PIERCE(BulletParticles.Entity::pierce),
        KILL(BulletParticles.Entity::kill)
    }

    private class ParticleEmitter(
        val particle: BulletParticles.Particle,
        val options: ParticleOptions,
        val coordinates: Vec3,
        val deltaCoordinates: Vec3,
        var remainingDuration: Int
    )
}