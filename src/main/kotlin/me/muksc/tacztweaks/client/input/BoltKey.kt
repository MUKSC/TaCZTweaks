package me.muksc.tacztweaks.client.input

import com.mojang.blaze3d.platform.InputConstants
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator
import me.muksc.tacztweaks.TaCZTweaks
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft

object BoltKey {
    @JvmField
    val KEY = KeyMapping(
        TaCZTweaks.key("bolt"),
        InputConstants.UNKNOWN.value,
        TaCZTweaks.keyCategory("mod")
    )

    fun onEndClientTick() {
        val player = Minecraft.getInstance().player ?: return
        while (KEY.consumeClick()) {
            IClientPlayerGunOperator.fromLocalPlayer(player).bolt()
        }
    }
}