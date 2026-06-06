package me.muksc.tacztweaks.feature.sound_physics_evaluate

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager
import me.muksc.tacztweaks.core.modVersionRange
import net.minecraft.client.Minecraft

object SoundPhysicsManager : ModCompatibilityManager(
    modId = "sound_physics_remastered",
    mixinPackagePrefix = "me.muksc.tacztweaks.mixin.feature.sound_physics_evaluate"
) {
    fun play(minecraft: Minecraft, x: Double, y: Double, z: Double, callback: Callback) = withFallback(Unit) {
        Inner.play(minecraft, x, y, z, callback)
    }

    @JvmStatic
    fun getProcessing(): SoundPhysicsEvaluationSoundInstance? =
        Inner.processing.get()

    @JvmStatic
    fun runProcessing(sound: SoundPhysicsEvaluationSoundInstance, block: Runnable) {
        Inner.runProcessing(sound, block)
    }

    @JvmStatic
    fun onEvaluationComplete(sound: SoundPhysicsEvaluationSoundInstance, result: EvaluationResult) {
        Inner.onEvaluationComplete(sound, result)
    }

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean = when (mixinClassName) {
        //~ if 1.21.1 '1.20.1' -> '1.21.1' {
        "me.muksc.tacztweaks.mixin.feature.sound_physics_evaluate.SoundPhysicsMixin_1_5_x" -> modVersionRange("sound_physics_remastered", "1.20.1-1.5.0")
        "me.muksc.tacztweaks.mixin.feature.sound_physics_evaluate.SoundPhysicsMixin_1_1_x" -> modVersionRange("sound_physics_remastered", "1.20.1-1.1.0", "1.20.1-1.5.0")
        //~}

        else -> true
    }

    fun interface Callback {
        fun onEvaluationComplete(result: EvaluationResult)
    }

    data class EvaluationResult(
        val airspace: Float,
        val occlusionAccumulation: Float,
        val reflectivity: Float
    ) {
        fun withAirspace(value: Float) = copy(airspace = value)
        fun withOcclusionAccumulation(value: Float) = copy(occlusionAccumulation = value)
        fun withReflectivity(value: Float) = copy(reflectivity = value)
    }

    private object Inner {
        private val callbacks = Object2ObjectOpenHashMap<SoundPhysicsEvaluationSoundInstance, Callback>()
        val processing: ThreadLocal<SoundPhysicsEvaluationSoundInstance?> = ThreadLocal()

        fun play(minecraft: Minecraft, x: Double, y: Double, z: Double, callback: Callback) {
            val sound = SoundPhysicsEvaluationSoundInstance(x, y, z)
            callbacks[sound] = callback
            minecraft.soundManager.play(sound)
        }

        fun runProcessing(sound: SoundPhysicsEvaluationSoundInstance, block: Runnable) {
            try {
                processing.set(sound)
                block.run()
            } finally {
                processing.set(null)
                callbacks.remove(sound)
            }
        }

        fun onEvaluationComplete(sound: SoundPhysicsEvaluationSoundInstance, result: EvaluationResult) {
            callbacks[sound]?.onEvaluationComplete(result)
        }
    }
}