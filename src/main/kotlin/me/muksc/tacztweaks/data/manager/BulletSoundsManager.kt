package me.muksc.tacztweaks.data.manager

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import com.tacz.guns.entity.EntityKineticBullet
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.anyOrEmpty
import me.muksc.tacztweaks.compat.soundphysics.network.message.ServerMessageAirspaceSounds
import me.muksc.tacztweaks.compat.soundphysics.network.message.ServerMessageSoundPhysicsRequiredStatus
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.data.BulletSounds
import me.muksc.tacztweaks.mixininterface.features.EntityKineticBulletExtension
import me.muksc.tacztweaks.network.NetworkHandler
import me.muksc.tacztweaks.thenPrioritizeBy
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

private val COMPARATOR = compareBy<BulletSounds> { it.priority }
    .thenPrioritizeBy { it.target.isNotEmpty() }
    .thenPrioritizeBy { when (it) {
        is BulletSounds.Block -> it.blocks.isNotEmpty()
        is BulletSounds.Entity -> it.entities.isNotEmpty()
        is BulletSounds.Whizz -> false
        is BulletSounds.Constant -> false
        is BulletSounds.AirSpace -> false
    } }

object BulletSoundsManager : BaseDataManager<BulletSounds>("bullet_sounds", COMPARATOR) {
    override fun notifyPlayer(player: ServerPlayer) {
        if (byType<BulletSounds.AirSpace>().isNotEmpty()) NetworkHandler.sendS2C(player, ServerMessageSoundPhysicsRequiredStatus)
        if (hasError()) {
            player.sendSystemMessage(TaCZTweaks.message()
                .append(TaCZTweaks.translatable("bullet_sounds.error").withStyle(ChatFormatting.RED)))
        }
    }

    override fun debugEnabled(): Boolean = Config.Debug.bulletSounds()

    override fun parseElement(json: JsonElement): BulletSounds =
        BulletSounds.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false) { /* Nothing */ }

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

    fun handleBlockSound(type: EBlockSoundType, level: ServerLevel, entity: EntityKineticBullet, result: BlockHitResult, state: BlockState) {
        val (id, sounds) = getSound(entity, result.location, BulletSounds.Block::blocks) {
            it.test(level, result.blockPos, state)
        } ?: return
        logDebug { "Using block bullet sounds: $id" }
        for (sound in type.getSound(sounds)) {
            if (!sound.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(result.location)) }) continue
            if (!sound.blocks.anyOrEmpty { it.test(level, result.blockPos, state) }) continue
            sound.play(level, result.location, entity)
        }
    }

    fun handleEntitySound(type: EEntitySoundType, level: ServerLevel, entity: EntityKineticBullet, location: Vec3, target: Entity) {
        val (id, sounds) = getSound(entity, location, BulletSounds.Entity::entities) {
            it.test(target)
        } ?: return
        logDebug { "Using entity bullet sounds: $id" }
        for (sound in type.getSound(sounds)) {
            if (!sound.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(location)) }) continue
            if (!sound.entities.anyOrEmpty { it.test(target) }) continue
            sound.play(level, location, entity)
        }
    }

    fun handleConstant(level: ServerLevel, entity: EntityKineticBullet) {
        val location = entity.position()
        val (id, sounds) = getSound<BulletSounds.Constant>(entity, location) ?: return
        logDebug { "Using constant bullet sounds: $id" }
        if (entity.tickCount % sounds.interval != 0) return
        for (sound in sounds.sounds) {
            if (!sound.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(location)) }) continue
            sound.play(level, location, entity)
        }
    }

    fun handleSoundWhizz(level: ServerLevel, entity: EntityKineticBullet, ignores: List<ServerPlayer>) {
        for (player in level.server.playerList.players) {
            if (entity.owner == player || player in ignores) continue
            if (player.level().dimension() != level.dimension()) continue
            handleSoundWhizz(player, entity)
        }
    }

    fun handleSoundWhizz(player: ServerPlayer, entity: EntityKineticBullet) {
        val ext = entity as EntityKineticBulletExtension
        val destination = ext.`tacztweaks$getPosition`()
        val (id, sounds) = getSound<BulletSounds.Whizz>(entity, destination) ?: return
        logDebug { "Using whizz bullet sounds '$id' for player '$player'" }

        val currentPosition = entity.position()
        val playerPosition = player.eyePosition
        val trajectory = destination.subtract(currentPosition)
        val length = playerPosition.subtract(currentPosition).dot(trajectory) / trajectory.lengthSqr()
        if (length !in 0.0..1.0) return

        val position = currentPosition.add(trajectory.scale(length))
        val distance = playerPosition.distanceTo(position)
        val whizz = sounds.sounds.firstOrNull { distance <= it.threshold } ?: return
        for (sound in whizz.sound) {
            if (!sound.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(destination)) }) continue
            sound.play(player, position, entity)
        }
    }

    fun handleAirspace(level: ServerLevel, entity: EntityKineticBullet) {
        val soundsList = getSounds<BulletSounds.AirSpace>(entity, entity.position()).takeIf { it.isNotEmpty() } ?: return
        for (player in level.server.playerList.players) {
            if (player.level().dimension() != level.dimension()) continue
            val distance = player.position().distanceTo(entity.position())
            val candidates = soundsList.mapNotNull { (id, sounds) ->
                val sound = sounds.sounds.firstOrNull { distance <= it.threshold } ?: return@mapNotNull null
                logDebug { "Using airspace bullet sounds '$id' for player '$player'" }
                sounds to sound
            }
            NetworkHandler.sendS2C(player, ServerMessageAirspaceSounds(candidates.map { (sounds, airspace) ->
                @Suppress("DEPRECATION")
                val packets = airspace.sound.filter { sound ->
                    sound.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(entity.position())) }
                }.map {
                    val soundEvent = if (it.range == null) SoundEvent.createVariableRangeEvent(it.sound) else SoundEvent.createFixedRangeEvent(it.sound, it.range)
                    ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent),
                        entity.soundSource,
                        entity.x,
                        entity.y,
                        entity.z,
                        it.volume,
                        it.pitch,
                        player.random.nextLong()
                    )
                }
                ServerMessageAirspaceSounds.AirspaceSound(
                    packets,
                    sounds.airspace.min.toFloat(),
                    sounds.airspace.max.toFloat(),
                    sounds.occlusion.min.toFloat(),
                    sounds.occlusion.max.toFloat(),
                    sounds.reflectivity.min.toFloat(),
                    sounds.reflectivity.max.toFloat()
                )
            }, entity.x, entity.y, entity.z))
        }
    }

    @Suppress("DEPRECATION")
    private fun BulletSounds.Sound.play(player: ServerPlayer, position: Vec3, entity: EntityKineticBullet) {
        val soundEvent = if (range == null) SoundEvent.createVariableRangeEvent(sound) else SoundEvent.createFixedRangeEvent(sound, range)
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
        val soundEvent = if (range == null) SoundEvent.createVariableRangeEvent(sound) else SoundEvent.createFixedRangeEvent(sound, range)
        level.playSound(null, position.x, position.y, position.z, soundEvent, entity.soundSource, volume, pitch)
    }

    enum class EBlockSoundType(val getSound: (BulletSounds.Block) -> List<BulletSounds.Block.BlockSound>) {
        HIT(BulletSounds.Block::hit),
        PIERCE(BulletSounds.Block::pierce),
        BREAK(BulletSounds.Block::`break`)
    }

    enum class EEntitySoundType(val getSound: (BulletSounds.Entity) -> List<BulletSounds.Entity.EntitySound>) {
        HIT(BulletSounds.Entity::hit),
        PIERCE(BulletSounds.Entity::pierce),
        KILL(BulletSounds.Entity::kill)
    }
}