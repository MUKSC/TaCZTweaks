package me.muksc.tacztweaks.feature.general.compatibility

import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager
import net.minecraft.world.entity.player.Player

//? if 1.20.1 && forge
import com.lazrproductions.cuffed.api.CuffedAPI

object CuffedManager : ModCompatibilityManager(
    modId = "cuffed",
    mixinPackagePrefix = null
) {
    @JvmStatic
    fun isItemUseDisabled(player: Player): Boolean = withFallback(false) {
        Inner.restraintsDisabledItemUse(player)
    }

    private object Inner {
        fun restraintsDisabledItemUse(player: Player): Boolean {
            //? if 1.20.1 && forge {
            val capability = CuffedAPI.Capabilities.getRestrainableCapability(player)
            return capability.restraintsDisabledItemUse()
            //?} else {
            /*return false
            *///?}
        }
    }
}