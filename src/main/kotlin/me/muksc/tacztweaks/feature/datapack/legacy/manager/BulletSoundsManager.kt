package me.muksc.tacztweaks.feature.datapack.legacy.manager

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import com.tacz.guns.entity.EntityKineticBullet
import com.tacz.guns.entity.EntityKineticBullet.EntityResult
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.core.anyOrEmpty
import me.muksc.tacztweaks.feature.datapack.legacy.BulletSounds
import me.muksc.tacztweaks.feature.raytracer.BulletHandler
import me.muksc.tacztweaks.mixininterface.feature.raytracer.RayTracingBullet
import me.muksc.tacztweaks.mixininterop.currentHitPosition
import me.muksc.tacztweaks.network.NetworkManager
import me.muksc.tacztweaks.network.message.ServerMessageAirspaceSounds
import me.muksc.tacztweaks.network.message.ServerMessageSoundPhysicsRequired
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

//? if <1.20.5
import me.muksc.tacztweaks.core.extension.getOrThrow

private val COMPARATOR = compareBy<BulletSounds> { it.priority }
    .thenByDescending { it.target.isNotEmpty() }
    .thenByDescending { when (it) {
        is BulletSounds.Block -> it.blocks.isNotEmpty()
        is BulletSounds.Entity -> it.entities.isNotEmpty()
        is BulletSounds.Whizz -> false
        is BulletSounds.Constant -> false
        is BulletSounds.Airspace -> false
    } }

object BulletSoundsManager : BaseDataManager<BulletSounds>("bullet_sounds", COMPARATOR) {
    private val ID = TaCZTweaks.id("bullet_sounds")
    override fun id(): ResourceLocation = ID

    override val debugEnabled: Boolean get() = Config.General.Debug.bulletSounds()

    override fun parseElement(json: JsonElement): BulletSounds =
        BulletSounds.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()

    override fun notifyPlayer(player: ServerPlayer) {
        if (byType<BulletSounds.Airspace>().isNotEmpty()) {
            NetworkManager.sendS2C(player, ServerMessageSoundPhysicsRequired)
        }
        super.notifyPlayer(player)
    }

    private inline fun <reified T : BulletSounds> getSounds(
        entity: EntityKineticBullet,
        location: Vec3
    ) : List<Pair<ResourceLocation, T>> = byType<T>().entries.filter { (_, sounds) ->
        sounds.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(location)) }
    }.map { it.toPair() }

    private inline fun <reified T : BulletSounds> getSound(
        entity: EntityKineticBullet,
        location: Vec3
    ): Pair<ResourceLocation, T>? = byType<T>().entries.firstOrNull { (_, sounds) ->
        sounds.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(location)) }
    }?.toPair()

    private inline fun <reified T : BulletSounds, E> getSound(
        entity: EntityKineticBullet,
        location: Vec3,
        selector: (T) -> List<E>,
        predicate: (E) -> Boolean
    ): Pair<ResourceLocation, T>? = byType<T>().entries.firstOrNull { (_, sounds) ->
        sounds.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(location)) }
            && selector(sounds).anyOrEmpty(predicate)
    }?.toPair()

    fun handleBlockSound(interactionResult: BulletHandler.InteractionResult, bullet: EntityKineticBullet, result: BlockHitResult, state: BlockState) {
        val level = bullet.level() as? ServerLevel ?: return
        val (id, sounds) = getSound(bullet, result.location, BulletSounds.Block::blocks) {
            it.test(level, result.blockPos, state)
        } ?: return
        logger.infoDebug("Using block bullet sounds: $id")
        for (sound in EBlockSoundType.of(interactionResult).getSound(sounds)) {
            if (!sound.target.anyOrEmpty { it.test(bullet, bullet.gunId, bullet.getDamage(result.location)) }) continue
            if (!sound.blocks.anyOrEmpty { it.test(level, result.blockPos, state) }) continue
            sound.play(level, result.location, bullet)
        }
    }

    fun handleEntitySound(interactionResult: BulletHandler.InteractionResult, bullet: EntityKineticBullet, result: EntityResult) {
        val level = bullet.level() as? ServerLevel ?: return
        val (id, sounds) = getSound(bullet, result.hitPos, BulletSounds.Entity::entities) {
            it.test(result.entity)
        } ?: return
        logger.infoDebug("Using entity bullet sounds: $id")
        for (sound in EEntitySoundType.of(interactionResult).getSound(sounds)) {
            if (!sound.target.anyOrEmpty { it.test(bullet, bullet.gunId, bullet.getDamage(result.hitPos)) }) continue
            if (!sound.entities.anyOrEmpty { it.test(result.entity) }) continue
            sound.play(level, result.hitPos, bullet)
        }
    }

    fun handleConstant(bullet: EntityKineticBullet) {
        val level = bullet.level() as? ServerLevel ?: return
        val location = bullet.position()
        val (id, sounds) = getSound<BulletSounds.Constant>(bullet, location) ?: return
        logger.infoDebug( "Using constant bullet sounds: $id")
        if (bullet.tickCount % sounds.interval != 0) return
        for (sound in sounds.sounds) {
            if (!sound.target.anyOrEmpty { it.test(bullet, bullet.gunId, bullet.getDamage(location)) }) continue
            sound.play(level, location, bullet)
        }
    }

    fun handleSoundWhizz(bullet: EntityKineticBullet, ignores: List<ServerPlayer>) {
        val level = bullet.level() as? ServerLevel ?: return
        for (player in level.server.playerList.players) {
            if (bullet.owner == player || player in ignores) continue
            if (player.level().dimension() != level.dimension()) continue
            handleSoundWhizz(player, bullet)
        }
    }

    fun handleSoundWhizz(player: ServerPlayer, bullet: EntityKineticBullet) {
        val ext = RayTracingBullet.of(bullet)
        val destination = ext.currentHitPosition
        val (id, sounds) = getSound<BulletSounds.Whizz>(bullet, destination) ?: return
        logger.infoDebug( "Using whizz bullet sounds '$id' for player '$player'")

        val currentPosition = bullet.position()
        val playerPosition = player.eyePosition
        val trajectory = destination.subtract(currentPosition)
        val length = playerPosition.subtract(currentPosition).dot(trajectory) / trajectory.lengthSqr()
        if (length !in 0.0..1.0) return

        val position = currentPosition.add(trajectory.scale(length))
        val distance = playerPosition.distanceTo(position)
        val whizz = sounds.sounds.firstOrNull { distance <= it.threshold } ?: return
        for (sound in whizz.sound) {
            if (!sound.target.anyOrEmpty { it.test(bullet, bullet.gunId, bullet.getDamage(destination)) }) continue
            sound.play(player, position, bullet)
        }
    }

    fun handleAirspace(bullet: EntityKineticBullet) {
        val level = bullet.level() as? ServerLevel ?: return
        val soundsList = getSounds<BulletSounds.Airspace>(bullet, bullet.position())
            .takeIf { it.isNotEmpty() } ?: return
        for (player in level.server.playerList.players) {
            if (player.level().dimension() != level.dimension()) continue
            val distance = player.position().distanceTo(bullet.position())
            val candidates = soundsList.mapNotNull { (id, sounds) ->
                val sound = sounds.sounds.firstOrNull { distance <= it.threshold } ?: return@mapNotNull null
                logger.infoDebug( "Using airspace bullet sounds '$id' for player '$player'")
                sounds to sound
            }
            NetworkManager.sendS2C(player, ServerMessageAirspaceSounds(candidates.map { (sounds, airspace) ->
                val packets = airspace.sound.filter { sound ->
                    sound.target.anyOrEmpty { it.test(bullet, bullet.gunId, bullet.getDamage(bullet.position())) }
                }.map {
                    val soundEvent = if (it.range == null) {
                        SoundEvent.createVariableRangeEvent(it.sound)
                    } else {
                        SoundEvent.createFixedRangeEvent(it.sound, it.range)
                    }
                    @Suppress("DEPRECATION")
                    ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent),
                        bullet.soundSource,
                        bullet.x,
                        bullet.y,
                        bullet.z,
                        it.volume,
                        it.pitch,
                        player.random.nextLong()
                    )
                }
                ServerMessageAirspaceSounds.AirspaceSound(
                    packets,
                    sounds.airspace,
                    sounds.occlusion,
                    sounds.reflectivity
                )
            }, bullet.x, bullet.y, bullet.z))
        }
    }

    private fun BulletSounds.Sound.play(player: ServerPlayer, position: Vec3, entity: EntityKineticBullet) {
        val soundEvent = if (range == null) SoundEvent.createVariableRangeEvent(sound) else SoundEvent.createFixedRangeEvent(sound, range)
        @Suppress("DEPRECATION")
        player.connection.send(ClientboundSoundPacket(
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent),
            entity.soundSource,
            position.x,
            position.y,
            position.z,
            volume,
            pitch,
            player.random.nextLong()
        ))
    }

    private fun BulletSounds.Sound.play(level: ServerLevel, position: Vec3, entity: EntityKineticBullet) {
        val soundEvent = if (range == null) {
            SoundEvent.createVariableRangeEvent(sound)
        } else {
            SoundEvent.createFixedRangeEvent(sound, range)
        }
        level.playSound(
            null,
            position.x, position.y, position.z,
            soundEvent, entity.soundSource,
            volume, pitch
        )
    }

    enum class EBlockSoundType(val getSound: (BulletSounds.Block) -> List<BulletSounds.Block.BlockSound>) {
        HIT(BulletSounds.Block::hit),
        PIERCE(BulletSounds.Block::pierce),
        BREAK(BulletSounds.Block::`break`);

        companion object {
            fun of(result: BulletHandler.InteractionResult): EBlockSoundType = when {
                result.success -> BREAK
                result.pierce -> PIERCE
                else -> HIT
            }
        }
    }

    enum class EEntitySoundType(val getSound: (BulletSounds.Entity) -> List<BulletSounds.Entity.EntitySound>) {
        HIT(BulletSounds.Entity::hit),
        PIERCE(BulletSounds.Entity::pierce),
        KILL(BulletSounds.Entity::kill);

        companion object {
            fun of(result: BulletHandler.InteractionResult): EEntitySoundType = when {
                result.success -> KILL
                result.pierce -> PIERCE
                else -> HIT
            }
        }
    }
}