package me.muksc.tacztweaks.core.compatibility

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.isModLoaded
import me.muksc.tacztweaks.core.logger.withMarker
import me.muksc.tacztweaks.feature.general.compatibility.CuffedManager
import me.muksc.tacztweaks.feature.general.compatibility.FirstAidManager
import me.muksc.tacztweaks.feature.general.compatibility.LRTacticalManager
import me.muksc.tacztweaks.feature.general.compatibility.LegendarySurvivalOverhaulManager
import me.muksc.tacztweaks.feature.general.compatibility.MTSManager
import me.muksc.tacztweaks.feature.general.compatibility.PillagersGunManager
import me.muksc.tacztweaks.feature.general.compatibility.SableManager
import me.muksc.tacztweaks.feature.general.compatibility.ValkyrienSkiesManager
import me.muksc.tacztweaks.feature.sound_physics_evaluate.SoundPhysicsManager
import org.slf4j.MarkerFactory

abstract class ModCompatibilityManager(
    val modId: String,
    val mixinPackagePrefix: String?
) {
    companion object {
        val ALL by lazy { listOf(
            CuffedManager,
            FirstAidManager,
            LegendarySurvivalOverhaulManager,
            LRTacticalManager,
            MTSManager,
            PillagersGunManager,
            SableManager,
            ValkyrienSkiesManager,

            SoundPhysicsManager
        ) }
    }

    @JvmField val logger = TaCZTweaks.logger.withMarker(
        MarkerFactory.getMarker("ModCompatibilityManager")
    )
    @JvmField val loaded = isModLoaded(modId)
    var hasError = false
        protected set

    fun onError(message: String, throwable: Throwable? = null) {
        hasError = true
        logger.error(message, throwable)
    }

    fun <T> withFallback(fallback: T, block: () -> T): T {
        if (!loaded) return fallback
        return try {
            block()
        } catch (e: Exception) {
            onError("Encountered an error while executing a '${modId}' mod compatibility code", e)
            fallback
        }
    }

    open fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean = true
}