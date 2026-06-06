package me.muksc.tacztweaks.feature.general.compatibility

import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager
import me.muksc.tacztweaks.core.isModLoaded

object ValkyrienSkiesManager : ModCompatibilityManager(
    modId = "valkyrienskies",
    mixinPackagePrefix = "me.muksc.tacztweaks.mixin.feature.general.compatibility.vs"
) {
    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        return when (mixinClassName) {
            "$mixinPackagePrefix.MixinLevelRendererMixin" -> !isModLoaded("vs_addition")
            else -> true
        }
    }
}