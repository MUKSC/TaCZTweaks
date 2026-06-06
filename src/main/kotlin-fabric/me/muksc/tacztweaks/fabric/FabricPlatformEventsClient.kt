package me.muksc.tacztweaks.fabric

import cn.sh1rocu.tacz.api.event.ClientPlayerNetworkEvent
import me.muksc.tacztweaks.platform.PlatformEventsClient
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object FabricPlatformEventsClient : PlatformEventsClient {
    override fun onClientLoggingOut(callback: () -> Unit) {
        ClientPlayerNetworkEvent.LOGGING_OUT.register { event ->
            callback()
        }
    }

    override fun onEndClientTick(callback: () -> Unit) {
        ClientTickEvents.END_CLIENT_TICK.register { minecraft ->
            callback()
        }
    }
}