package me.muksc.tacztweaks.forge

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.Config
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ConfigScreenHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT

@Mod(TaCZTweaks.MOD_ID)
object TaCZTweaksMod {
    init {
        TaCZTweaks.initialize(ForgePlatform)
        if (FMLEnvironment.dist == Dist.CLIENT) TaCZTweaks.initializeClient(ForgePlatformClient)
        LOADING_CONTEXT.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory::class.java) {
            ConfigScreenHandler.ConfigScreenFactory { client, screen -> Config.generateConfigScreen(screen) }
        }
    }
}