package me.muksc.tacztweaks.data

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.brigadier.StringReader
import com.mojang.logging.LogUtils
import com.mojang.serialization.JsonOps
import com.tacz.guns.entity.EntityKineticBullet
import me.muksc.tacztweaks.mixininterface.features.EntityKineticBulletExtension
import net.minecraft.commands.arguments.ParticleArgument
import net.minecraft.commands.arguments.coordinates.LocalCoordinates
import net.minecraft.commands.arguments.coordinates.WorldCoordinate
import net.minecraft.commands.arguments.coordinates.WorldCoordinates
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.minecraftforge.registries.ForgeRegistries
import kotlin.reflect.KClass

private val GSON = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

object BulletParticlesManager : SimpleJsonResourceReloadListener(GSON, "bullet_particles") {
    private val LOGGER = LogUtils.getLogger()
    private var error = false
    private var bulletParticles: Map<KClass<*>, Map<ResourceLocation, BulletParticles>> = emptyMap()

    fun hasError(): Boolean = error

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : BulletParticles> byType(): Map<ResourceLocation, T> =
        bulletParticles.getOrElse(T::class) { emptyMap() } as Map<ResourceLocation, T>

    private inline fun <reified T : BulletParticles, E> getParticle(
        entity: EntityKineticBullet,
        location: Vec3,
        selector: (T) -> List<E>,
        predicate: (E) -> Boolean
    ): T? = byType<T>().values.run {
        filter { particles -> particles.target.any { it.test(entity, location) } }.run {
            firstOrNull { particles ->
                selector.invoke(particles).any(predicate)
            } ?: firstOrNull { particles ->
                selector.invoke(particles).isEmpty()
            }
        } ?: filter { particles -> particles.target.isEmpty() }.run {
            firstOrNull { particles ->
                selector.invoke(particles).any(predicate)
            } ?: firstOrNull { particles ->
                selector.invoke(particles).isEmpty()
            }
        }
    }

    override fun apply(
        map: Map<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profileFiller: ProfilerFiller,
    ) {
        val bulletParticles = mutableMapOf<KClass<*>, MutableMap<ResourceLocation, BulletParticles>>()
        for ((resourceLocation, element) in map) {
            try {
                val particles = BulletParticles.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow(false) { /* Nothing */ }
                bulletParticles.computeIfAbsent(particles::class) { mutableMapOf() }[resourceLocation] = particles
            } catch (e: RuntimeException) {
                LOGGER.error("Parsing error loading bullet particles $resourceLocation $e")
                error = true
            }
        }
        this.bulletParticles = bulletParticles
    }

    fun handleBlockParticle(type: EBlockParticleType, level: ServerLevel, entity: EntityKineticBullet, location: Vec3, state: BlockState) {
        val particles = getParticle(entity, location, BulletParticles.Block::blocks) {
            it.test(state)
        }?.run(type.getParticle) ?: return
        for (particle in particles) {
            val id = ForgeRegistries.BLOCKS.getKey(state.block)?.toString()
            particle.summon(level.server, entity, id)
        }
    }

    fun handleEntityParticle(type: EEntityParticleType, level: ServerLevel, entity: EntityKineticBullet, location: Vec3, target: Entity) {
        val particles = getParticle(entity, location, BulletParticles.Entity::entities) {
            it.test(target)
        }?.run(type.getParticle) ?: return
        for (particle in particles) {
            particle.summon(level.server, entity)
        }
    }

    private fun BulletParticles.Particle.summon(server: MinecraftServer, entity: EntityKineticBullet, context: String? = null) {
        val level = entity.level() as? ServerLevel ?: return
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
        for (player in level.players()) {
            level.sendParticles(
                player,
                particleOptions,
                force,
                coordinates.x,
                coordinates.y,
                coordinates.z,
                count,
                deltaCoordinates.x,
                deltaCoordinates.y,
                deltaCoordinates.z,
                speed
            )
        }
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