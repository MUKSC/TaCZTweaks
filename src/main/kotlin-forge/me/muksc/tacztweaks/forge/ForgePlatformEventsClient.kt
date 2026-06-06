package me.muksc.tacztweaks.forge

import me.muksc.tacztweaks.platform.PlatformEventsClient
import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.event.TickEvent
import thedarkcolour.kotlinforforge.forge.FORGE_BUS

object ForgePlatformEventsClient : PlatformEventsClient {
    override fun onClientLoggingOut(callback: () -> Unit) {
        FORGE_BUS.addListener<ClientPlayerNetworkEvent.LoggingOut> {
            callback()
        }
    }

    override fun onEndClientTick(callback: () -> Unit) {
        FORGE_BUS.addListener<TickEvent.ClientTickEvent> { event ->
            if (event.phase != TickEvent.Phase.END) return@addListener
            callback()
        }
    }
}