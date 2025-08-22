package me.muksc.tacztweaks.data

import com.google.common.collect.ImmutableMap
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.brigadier.StringReader
import com.mojang.logging.LogUtils
import com.mojang.serialization.JsonOps
import com.tacz.guns.entity.EntityKineticBullet
import me.muksc.tacztweaks.Config
import me.muksc.tacztweaks.mixininterface.features.EntityKineticBulletExtension
import me.muksc.tacztweaks.thenPrioritizeBy
import me.muksc.tacztweaks.toImmutableMap
import net.minecraft.commands.arguments.ParticleArgument
import net.minecraft.commands.arguments.coordinates.LocalCoordinates
import net.minecraft.commands.arguments.coordinates.WorldCoordinate
import net.minecraft.commands.arguments.coordinates.WorldCoordinates
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.registries.ForgeRegistries
import kotlin.collections.firstOrNull
import kotlin.reflect.KClass

private val GSON = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

object BulletParticlesManager : SimpleJsonResourceReloadListener(GSON, "bullet_particles") {
    private val LOGGER = LogUtils.getLogger()
    private var error = false
    private var bulletParticles: Map<KClass<*>, Map<ResourceLocation, BulletParticles>> = emptyMap()
    private val emitters: MutableList<ParticleEmitter> = mutableListOf()

    private class ParticleEmitter(
        val particle: BulletParticles.Particle,
        val options: ParticleOptions,
        val coordinates: Vec3,
        val deltaCoordinates: Vec3,
        var remainingDuration: Int
    )

    fun hasError(): Boolean = error

    private fun debug(msg: () -> String) {
        if (Config.Debug.bulletParticles()) LOGGER.info(msg.invoke())
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : BulletParticles> byType(): Map<ResourceLocation, T> =
        bulletParticles.getOrElse(T::class) { emptyMap() } as Map<ResourceLocation, T>

    private inline fun <reified T : BulletParticles, E> getParticle(
        entity: EntityKineticBullet,
        location: Vec3,
        selector: (T) -> List<E>,
        predicate: (E) -> Boolean
    ): Pair<ResourceLocation, T>? = byType<T>().entries.firstOrNull { (_, particles) ->
        (particles.target.isEmpty() || particles.target.any { it.test(entity, entity.gunId, entity.getDamage(location)) })
                && (selector(particles).isEmpty() || selector(particles).any(predicate))
    }?.toPair()

    @Suppress("UnstableApiUsage")
    override fun apply(
        map: Map<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profileFiller: ProfilerFiller,
    ) {
        val bulletParticles = mutableMapOf<KClass<*>, ImmutableMap.Builder<ResourceLocation, BulletParticles>>()
        for ((resourceLocation, element) in map) {
            try {
                val particles = BulletParticles.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow(false) { /* Nothing */ }
                bulletParticles.computeIfAbsent(particles::class) { ImmutableMap.builder() }.put(resourceLocation, particles)
            } catch (e: RuntimeException) {
                LOGGER.error("Parsing error loading bullet particles $resourceLocation $e")
                error = true
            }
        }
        this.bulletParticles = bulletParticles.mapValues { entry -> entry.value.orderEntriesByValue(
            compareBy<BulletParticles> { it.priority }
                .thenPrioritizeBy { it.target.isNotEmpty() }
                .thenPrioritizeBy { when (it) {
                    is BulletParticles.Block -> it.blocks.isNotEmpty()
                    is BulletParticles.Entity -> it.entities.isNotEmpty()
                } }
        ).build() }.toImmutableMap()
    }

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
        debug { "Using block bullet particles: $id" }
        for (particle in type.getParticle(particles)) {
            val id = ForgeRegistries.BLOCKS.getKey(state.block)?.toString()
            particle.summon(level.server, entity, id)
        }
    }

    fun handleEntityParticle(type: EEntityParticleType, level: ServerLevel, entity: EntityKineticBullet, location: Vec3, target: Entity) {
        val (id, particles) = getParticle(entity, location, BulletParticles.Entity::entities) {
            it.test(target)
        } ?: return
        debug { "Using entity bullet particles: $id" }
        for (particle in type.getParticle(particles)) {
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

    enum class EBlockParticleType(val getParticle: (BulletParticles.Block) -> List<BulletParticles.Particle>) {
        HIT(BulletParticles.Block::hit),
        PIERCE(BulletParticles.Block::pierce),
        BREAK(BulletParticles.Block::`break`)
    }

    enum class EEntityParticleType(val getParticle: (BulletParticles.Entity) -> List<BulletParticles.Particle>) {
        HIT(BulletParticles.Entity::hit),
        PIERCE(BulletParticles.Entity::pierce),
        KILL(BulletParticles.Entity::kill)
    }
}