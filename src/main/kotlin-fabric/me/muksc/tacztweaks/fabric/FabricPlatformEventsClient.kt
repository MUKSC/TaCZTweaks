package me.muksc.tacztweaks.fabric

import me.muksc.tacztweaks.platform.PlatformEventsClient
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents

object FabricPlatformEventsClient : PlatformEventsClient {
    override fun onClientLoggingOut(callback: () -> Unit) {
        ClientPlayConnectionEvents.DISCONNECT.register { listener, minecraft ->
            callback()
        }
    }

    override fun onEndClientTick(callback: () -> Unit) {
        ClientTickEvents.END_CLIENT_TICK.register { minecraft ->
            callback()
        }
    }
}