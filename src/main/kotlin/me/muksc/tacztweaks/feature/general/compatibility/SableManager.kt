package me.muksc.tacztweaks.feature.general.compatibility

import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager
import me.muksc.tacztweaks.core.modVersionRange

object SableManager : ModCompatibilityManager(
    modId = "sable",
    mixinPackagePrefix = "me.muksc.tacztweaks.mixin.feature.general.compatibility.sable"
) {
    val taczSableCompatEnabled: () -> Boolean by lazy {
        try {
            Class.forName("com.tacz.guns.compat.sable.SableCompat").getMethod("isEnabled")
            Inner::taczSableCompatEnabled
        } catch (_: ReflectiveOperationException) {
            { false }
        }
    }

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean = when (mixinClassName) {
        "$mixinPackagePrefix.lrtactical" -> LRTacticalManager.loaded && modVersionRange(LRTacticalManager.modId, "")
        else -> true
    }

    private object Inner {
        fun taczSableCompatEnabled(): Boolean = /*? if 1.20.1 {*/ false /*?} else {*/ /*com.tacz.guns.compat.sable.SableCompat.isEnabled() *//*?}*/
    }
}