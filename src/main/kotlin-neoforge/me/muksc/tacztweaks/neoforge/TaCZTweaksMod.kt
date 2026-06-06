package me.muksc.tacztweaks.neoforge

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.Config
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT

@Mod(TaCZTweaks.MOD_ID)
object TaCZTweaksMod {
    init {
        TaCZTweaks.initialize(NeoForgePlatform)
        if (FMLEnvironment.dist.isClient) TaCZTweaks.initializeClient(NeoForgePlatformClient)
        LOADING_CONTEXT.registerExtensionPoint(IConfigScreenFactory::class.java) {
            IConfigScreenFactory { container, screen -> Config.generateConfigScreen(screen) }
        }
    }
}