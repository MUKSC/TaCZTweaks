package me.muksc.tacztweaks.data

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.mojang.logging.LogUtils
import com.mojang.serialization.JsonOps
import com.tacz.guns.entity.EntityKineticBullet
import me.muksc.tacztweaks.EntityKineticBulletExtension
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.LocalPlayer
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.profiling.ProfilerFiller

private val GSON = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

object BulletSoundsManager : SimpleJsonResourceReloadListener(GSON, "bullet_sounds") {
    private val LOGGER = LogUtils.getLogger()
    private var bulletSounds: Map<ResourceLocation, BulletSounds> = emptyMap()

    override fun apply(
        map: Map<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profileFiller: ProfilerFiller,
    ) {
        val bulletSounds = mutableMapOf<ResourceLocation, BulletSounds>()
        for ((resourceLocation, element) in map) {
            try {
                bulletSounds[resourceLocation] = BulletSounds.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow(true, LOGGER::error)
            } catch (e: JsonParseException) {
                LOGGER.error("Parsing error loading bullet sounds $resourceLocation $e")
            }
        }
        this.bulletSounds = bulletSounds
    }

    fun getSound(entity: EntityKineticBullet): BulletSounds? =
        bulletSounds.values.firstOrNull { sounds -> when (sounds.target.type) {
            Target.EType.GUN -> sounds.target.values.contains(entity.gunId)
            Target.EType.AMMO -> sounds.target.values.contains(entity.ammoId)
        } } ?: bulletSounds.values.firstOrNull { it.target.values.isEmpty() }

    fun handleSoundWhizz(level: ClientLevel, player: LocalPlayer, entity: EntityKineticBullet, partialTicks: Float) {
        if (entity.owner == player) return
        val ext = entity as EntityKineticBulletExtension
        if (ext.`tacztweaks$whizzed`()) return
        val sounds = getSound(entity) ?: return
        if (sounds.whizzes.isEmpty()) return

        val currentPosition = entity.getPosition(partialTicks)
        val playerPosition = player.getPosition(partialTicks)
        val trajectory = entity.deltaMovement
        val length = playerPosition.subtract(currentPosition).dot(trajectory) / trajectory.lengthSqr()
        if (length < 0) return

        val position = currentPosition.add(trajectory.scale(length))
        val distance = playerPosition.distanceTo(position)
        val whizz = sounds.whizzes.firstOrNull { distance <= it.threshold } ?: return
        val soundEvent = SoundEvent.createVariableRangeEvent(whizz.sound.sound)
        level.playLocalSound(position.x, position.y, position.z, soundEvent, SoundSource.MASTER, whizz.sound.volume, whizz.sound.pitch, false)
        ext.`tacztweaks$setWhizzed`()
    }
}