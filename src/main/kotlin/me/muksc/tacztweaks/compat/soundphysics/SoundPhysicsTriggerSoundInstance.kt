package me.muksc.tacztweaks.compat.soundphysics

import me.muksc.tacztweaks.TaCZTweaks
import net.minecraft.client.resources.sounds.AbstractSoundInstance
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import java.util.UUID

class SoundPhysicsTriggerSoundInstance(
    val uuid: UUID,
    x: Double,
    y: Double,
    z: Double
) : AbstractSoundInstance(
    TaCZTweaks.id("sound_physics_trigger"),
    SoundSource.MASTER,
    RandomSource.create()
) {
    init {
        this.x = x
        this.y = y
        this.z = z
    }
}