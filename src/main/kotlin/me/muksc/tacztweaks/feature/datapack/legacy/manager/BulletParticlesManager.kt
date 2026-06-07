package me.muksc.tacztweaks.feature.datapack.legacy.manager

import com.google.gson.JsonElement
import com.mojang.brigadier.StringReader
import com.mojang.serialization.JsonOps
import com.tacz.guns.entity.EntityKineticBullet
import com.tacz.guns.entity.EntityKineticBullet.EntityResult
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.core.anyOrEmpty
import me.muksc.tacztweaks.core.extension.id
import me.muksc.tacztweaks.feature.datapack.legacy.BulletParticles
import me.muksc.tacztweaks.feature.datapack.legacy.BulletParticles.Particle.Coordinates
import me.muksc.tacztweaks.feature.datapack.legacy.BulletParticles.Particle.Coordinates.ECoordinatesType
import me.muksc.tacztweaks.feature.raytracer.BulletHandler
import me.muksc.tacztweaks.mixininterface.feature.raytracer.RayTracingBullet
import me.muksc.tacztweaks.mixininterop.currentHitPosition
import net.minecraft.commands.arguments.ParticleArgument
import net.minecraft.commands.arguments.coordinates.LocalCoordinates
import net.minecraft.commands.arguments.coordinates.WorldCoordinate
import net.minecraft.commands.arguments.coordinates.WorldCoordinates
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.commands.arguments.coordinates.Coordinates as MCCoordinates

//? if <1.20.5 {
import me.muksc.tacztweaks.core.extension.getOrThrow
import net.minecraft.core.registries.Registries
//?}

private val COMPARATOR = compareBy<BulletParticles> { it.priority }
    .thenByDescending { it.target.isNotEmpty() }
    .thenByDescending { when (it) {
        is BulletParticles.Block -> it.blocks.isNotEmpty()
        is BulletParticles.Entity -> it.entities.isNotEmpty()
    } }

object BulletParticlesManager : BaseDataManager<BulletParticles>("bullet_particles", COMPARATOR) {
    private val ID = TaCZTweaks.id("bullet_particles")
    override fun id(): ResourceLocation = ID

    override val debugEnabled: Boolean get() = Config.General.Debug.bulletParticles()

    override fun parseElement(json: JsonElement): BulletParticles =
        BulletParticles.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()

    private inline fun <reified T : BulletParticles, E> getParticle(
        entity: EntityKineticBullet,
        location: Vec3,
        selector: (T) -> List<E>,
        predicate: (E) -> Boolean
    ): Pair<ResourceLocation, T>? = byType<T>().entries.firstOrNull { (_, particles) ->
        particles.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(location)) }
            && selector(particles).anyOrEmpty(predicate)
    }?.toPair()

    fun handleBlockParticle(interactionResult: BulletHandler.InteractionResult, bullet: EntityKineticBullet, result: BlockHitResult, state: BlockState) {
        val level = bullet.level() as? ServerLevel ?: return
        val (id, particles) = getParticle(bullet, result.location, BulletParticles.Block::blocks) {
            it.test(level, result.blockPos, state)
        } ?: return
        logger.infoDebug("Using block bullet particles: $id")
        for (particle in EBlockParticleType.of(interactionResult).getParticle(particles)) {
            if (!particle.target.anyOrEmpty { it.test(bullet, bullet.gunId, bullet.getDamage(result.location)) }) continue
            if (!particle.blocks.anyOrEmpty { it.test(level, result.blockPos, state) }) continue
            val id = state.block.id?.toString()
            particle.summon(level, bullet, id)
        }
    }

    fun handleEntityParticle(interactionResult: BulletHandler.InteractionResult, bullet: EntityKineticBullet, result: EntityResult) {
        val level = bullet.level() as? ServerLevel ?: return
        val (id, particles) = getParticle(bullet, result.hitPos, BulletParticles.Entity::entities) {
            it.test(result.entity)
        } ?: return
        logger.infoDebug( "Using entity bullet particles: $id")
        for (particle in EEntityParticleType.of(interactionResult).getParticle(particles)) {
            if (!particle.target.anyOrEmpty { it.test(bullet, bullet.gunId, bullet.getDamage(result.hitPos)) }) continue
            if (!particle.entities.anyOrEmpty { it.test(result.entity) }) continue
            particle.summon(level, bullet)
        }
    }

    private val emitters = Object2ObjectOpenHashMap<ResourceKey<Level>, ObjectArrayList<ParticleEmitter>>()

    fun onLevelTick(level: ServerLevel) {
        val list = emitters[level.dimension()] ?: return
        list.removeIf { emitter ->
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
            emitter.remainingDuration <= 0
        }
    }

    private fun BulletParticles.Particle.summon(level: ServerLevel, bullet: EntityKineticBullet, context: String? = null) {
        val ext = RayTracingBullet.of(bullet)
        val source = bullet.createCommandSourceStack()
            .withPosition(ext.currentHitPosition)
        val reader = StringReader(if (context != null) particle.format(context) else particle)
        //? if <1.20.5 {
        val registries = level.registryAccess().lookupOrThrow(Registries.PARTICLE_TYPE)
        //?} else {
        /*val registries = level.registryAccess()
        *///?}
        val particleOptions = ParticleArgument.readParticle(reader, registries)
        emitters.computeIfAbsent(level.dimension()) {
            ObjectArrayList()
        }.add(ParticleEmitter(
            this,
            particleOptions,
            position.toMinecraft().getPosition(source),
            delta.toMinecraft().getPosition(source),
            duration
        ))
    }

    private fun Coordinates.toMinecraft(): MCCoordinates = when (type) {
        ECoordinatesType.ABSOLUTE -> WorldCoordinates(
            WorldCoordinate(false, x),
            WorldCoordinate(false, y),
            WorldCoordinate(false, z)
        )
        ECoordinatesType.RELATIVE -> WorldCoordinates(
            WorldCoordinate(true, x),
            WorldCoordinate(true, y),
            WorldCoordinate(true, z)
        )
        ECoordinatesType.LOCAL -> LocalCoordinates(x, y, z)
    }

    enum class EBlockParticleType(val getParticle: (BulletParticles.Block) -> List<BulletParticles.Block.BlockParticle>) {
        HIT(BulletParticles.Block::hit),
        PIERCE(BulletParticles.Block::pierce),
        BREAK(BulletParticles.Block::`break`);

        companion object {
            fun of(result: BulletHandler.InteractionResult): EBlockParticleType = when {
                result.success -> BREAK
                result.pierce -> PIERCE
                else -> HIT
            }
        }
    }

    enum class EEntityParticleType(val getParticle: (BulletParticles.Entity) -> List<BulletParticles.Entity.EntityParticle>) {
        HIT(BulletParticles.Entity::hit),
        PIERCE(BulletParticles.Entity::pierce),
        KILL(BulletParticles.Entity::kill);

        companion object {
            fun of(result: BulletHandler.InteractionResult): EEntityParticleType = when {
                result.success -> KILL
                result.pierce -> PIERCE
                else -> HIT
            }
        }
    }

    private class ParticleEmitter(
        val particle: BulletParticles.Particle,
        val options: ParticleOptions,
        val coordinates: Vec3,
        val deltaCoordinates: Vec3,
        var remainingDuration: Int
    )
}