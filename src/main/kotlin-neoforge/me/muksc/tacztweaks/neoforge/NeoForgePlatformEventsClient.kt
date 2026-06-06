package me.muksc.tacztweaks.neoforge

import me.muksc.tacztweaks.platform.PlatformEventsClient
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS

object NeoForgePlatformEventsClient : PlatformEventsClient {
    override fun onClientLoggingOut(callback: () -> Unit) {
        FORGE_BUS.addListener<ClientPlayerNetworkEvent.LoggingOut> {
            callback()
        }
    }

    override fun onEndClientTick(callback: () -> Unit) {
        FORGE_BUS.addListener<ClientTickEvent.Post> { event ->
            callback()
        }
    }
}