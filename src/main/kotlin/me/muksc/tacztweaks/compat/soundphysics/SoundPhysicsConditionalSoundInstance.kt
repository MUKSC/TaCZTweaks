package me.muksc.tacztweaks.compat.soundphysics

import net.minecraft.client.resources.sounds.AbstractSoundInstance
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource

class SoundPhysicsConditionalSoundInstance(
    sound: SoundEvent,
    source: SoundSource,
    volume: Float,
    pitch: Float,
    random: RandomSource,
    x: Double,
    y: Double,
    z: Double,
    private val minAirspace: Float,
    private val maxAirspace: Float,
    private val minOcclusion: Float,
    private val maxOcclusion: Float,
    private val minReflectivity: Float,
    private val maxReflectivity: Float
) : AbstractSoundInstance(sound.location, source, random) {
    init {
        this.volume = volume
        this.pitch = pitch
        this.x = x
        this.y = y
        this.z = z
    }

    fun canPlayAtAirspace(airspace: Float): Boolean =
        airspace in minAirspace..maxAirspace

    fun canPlayAtOcclusion(occlusion: Double): Boolean =
        occlusion in minOcclusion..maxOcclusion

    fun canPlayAtReflectivity(reflectivity: Float): Boolean =
        reflectivity in minReflectivity..maxReflectivity
}