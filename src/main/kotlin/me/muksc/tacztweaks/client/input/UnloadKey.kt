package me.muksc.tacztweaks.client.input

import com.mojang.blaze3d.platform.InputConstants
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.network.NetworkManager
import me.muksc.tacztweaks.network.message.ClientMessagePlayerUnload
import net.minecraft.client.KeyMapping

object UnloadKey {
    @JvmField
    val KEY = KeyMapping(
        TaCZTweaks.key("unload"),
        InputConstants.UNKNOWN.value,
        TaCZTweaks.keyCategory("mod")
    )

    fun onEndClientTick() {
        if (!Config.KeyActions.Unload.enabled()) return
        while (KEY.consumeClick()) {
            NetworkManager.sendC2S(ClientMessagePlayerUnload)
        }
    }
}