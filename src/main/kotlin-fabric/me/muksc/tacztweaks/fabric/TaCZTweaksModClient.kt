package me.muksc.tacztweaks.fabric

import me.muksc.tacztweaks.TaCZTweaks
import net.fabricmc.api.ClientModInitializer

object TaCZTweaksModClient : ClientModInitializer {
    override fun onInitializeClient() {
        TaCZTweaks.initializeClient(FabricPlatformClient)
    }
}