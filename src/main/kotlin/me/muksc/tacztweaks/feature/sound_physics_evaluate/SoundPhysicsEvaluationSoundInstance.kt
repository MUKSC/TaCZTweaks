package me.muksc.tacztweaks.feature.sound_physics_evaluate

import me.muksc.tacztweaks.TaCZTweaks
import net.minecraft.client.resources.sounds.AbstractSoundInstance
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource

class SoundPhysicsEvaluationSoundInstance(
    x: Double,
    y: Double,
    z: Double
) : AbstractSoundInstance(
    TaCZTweaks.id("sound_physics_evaluation"),
    SoundSource.MASTER,
    RandomSource.create()
) {
    init {
        this.x = x
        this.y = y
        this.z = z
    }

    @JvmField var airspace: Float = 1.0F
    @JvmField var occlusionAccumulation: Double = 1.0
    @JvmField var reflectivity: Float = 1.0F
    @JvmField var reflectivityDivider: Int = 1
}