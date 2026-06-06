package me.muksc.tacztweaks.fabric

import me.muksc.tacztweaks.TaCZTweaks
import net.fabricmc.api.ModInitializer

object TaCZTweaksMod : ModInitializer {
    override fun onInitialize() {
        TaCZTweaks.initialize(FabricPlatform)
    }
}