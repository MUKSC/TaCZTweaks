package me.muksc.tacztweaks.compat.soundphysics

import me.muksc.tacztweaks.compat.soundphysics.network.message.ServerMessageConditionalAirspaceSound
import me.muksc.tacztweaks.network.NetworkHandler
import net.minecraftforge.fml.ModList

object SoundPhysicsCompat {
    const val FLAG = "sound_physics"
    private var enabled = false

    var currentSound: SoundPhysicsConditionalSoundInstance? = null
    var currentAirspace: Float? = null
    var currentOcclusionAccumulation: Double? = null
    var currentReflectivity: Float? = null
    var reflectivityDivider = 1

    fun isEnabled(): Boolean = enabled

    fun initialize() {
        if (ModList.get().isLoaded("sound_physics_remastered")) enabled = true
        NetworkHandler.registerS2C(ServerMessageConditionalAirspaceSound.TYPE, ServerMessageConditionalAirspaceSound.STREAM_CODEC, ServerMessageConditionalAirspaceSound::handle)
    }
}