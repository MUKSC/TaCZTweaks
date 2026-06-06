package me.muksc.tacztweaks.fabric.compat

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.muksc.tacztweaks.config.Config

object ModMenuCompat : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> =
        ConfigScreenFactory { Config.generateConfigScreen(it) }
}