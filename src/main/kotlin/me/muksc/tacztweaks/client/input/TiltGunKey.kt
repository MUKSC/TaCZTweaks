package me.muksc.tacztweaks.client.input

import com.mojang.blaze3d.platform.InputConstants
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.feature.synced_slide.shouldSlide
import net.minecraft.client.Minecraft
import net.minecraft.client.ToggleKeyMapping
import net.minecraft.client.player.LocalPlayer

object TiltGunKey {
    @JvmField
    val KEY = ToggleKeyMapping(
        TaCZTweaks.key("tiltGun"),
        InputConstants.UNKNOWN.value,
        TaCZTweaks.keyCategory("mod")
    ) { Config.KeyActions.TiltGun.type().isToggle() }

    @JvmStatic
    fun isActive(player: LocalPlayer): Boolean =
        KEY.isDown && player.shouldSlide

    fun onEndClientTick() {
        if (!Config.KeyActions.TiltGun.cancelSprint()) return
        val player = Minecraft.getInstance().player ?: return
        if (isActive(player)) player.isSprinting = false
    }
}