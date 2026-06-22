package me.muksc.tacztweaks.feature.general.compatibility

import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager
import me.muksc.tacztweaks.core.modVersionRange

object SableManager : ModCompatibilityManager(
    modId = "sable",
    mixinPackagePrefix = "me.muksc.tacztweaks.mixin.feature.general.compatibility.sable"
) {
    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean = when (mixinClassName) {
        "$mixinPackagePrefix.lrtactical" -> LRTacticalManager.loaded && modVersionRange(LRTacticalManager.modId, "")
        else -> true
    }
}