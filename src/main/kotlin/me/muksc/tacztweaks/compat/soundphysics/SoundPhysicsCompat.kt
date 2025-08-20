package me.muksc.tacztweaks.compat.soundphysics

import me.muksc.tacztweaks.compat.soundphysics.network.message.ServerMessageAirspaceSounds
import me.muksc.tacztweaks.network.NetworkHandler
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.ModList
import java.util.*

object SoundPhysicsCompat {
    private var enabled = false
    private val pendingSounds: MutableMap<UUID, ServerMessageAirspaceSounds> = mutableMapOf()
    var processingSound: ProcessingSound? = null

    fun isEnabled(): Boolean = enabled

    fun initialize() {
        if (ModList.get().isLoaded("sound_physics_remastered")) enabled = true
        NetworkHandler.registerS2C(ServerMessageAirspaceSounds.TYPE, ServerMessageAirspaceSounds.STREAM_CODEC, ServerMessageAirspaceSounds::handle)
    }

    fun play(minecraft: Minecraft, packet: ServerMessageAirspaceSounds) {
        val uuid = UUID.randomUUID()
        pendingSounds[uuid] = packet
        minecraft.soundManager.play(SoundPhysicsTriggerSoundInstance(uuid, packet.x, packet.y, packet.z))
    }

    fun onSoundEvaluationComplete() {
        val processing = processingSound ?: return
        val pending = pendingSounds[processing.sound.uuid] ?: return

        val airspace = processing.airspace ?: return
        val occlusionAccumulation = processing.occlusionAccumulation ?: return
        val reflectivity = processing.reflectivity?.div(processing.reflectivityDivider) ?: return
        val sound = pending.sounds.firstOrNull {
            it.canPlayAtAirspace(airspace)
                    && it.canPlayAtOcclusion(occlusionAccumulation)
                    && it.canPlayAtReflectivity(reflectivity)
        } ?: return

        val packet = sound.packet ?: return
        val minecraft = Minecraft.getInstance()
        minecraft.execute {
            minecraft.connection?.handleSoundEvent(packet)
        }
    }

    fun runProcessing(sound: SoundPhysicsTriggerSoundInstance, block: Runnable) {
        try {
            processingSound = ProcessingSound(sound)
            block.run()
        } finally {
            processingSound = null
            pendingSounds.remove(sound.uuid)
        }
    }

    class ProcessingSound(val sound: SoundPhysicsTriggerSoundInstance) {
        var airspace: Float? = null
        var occlusionAccumulation: Double? = null
        var reflectivity: Float? = null
        var reflectivityDivider = 1
    }
}