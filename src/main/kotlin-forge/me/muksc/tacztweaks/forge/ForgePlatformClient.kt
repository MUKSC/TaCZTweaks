package me.muksc.tacztweaks.forge

import me.muksc.tacztweaks.core.resource.IdentifiableResourceReloadListener
import me.muksc.tacztweaks.platform.PlatformClient
import me.muksc.tacztweaks.platform.PlatformEventsClient
import net.minecraft.client.KeyMapping
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

object ForgePlatformClient : PlatformClient {
    override val events: PlatformEventsClient get() = ForgePlatformEventsClient

    override fun registerKeyMappings(keys: List<KeyMapping>) {
        MOD_BUS.addListener<RegisterKeyMappingsEvent> { event ->
            for (key in keys) {
                event.register(key)
            }
        }
    }

    override fun registerClientReloadListeners(listeners: List<IdentifiableResourceReloadListener>) {
        MOD_BUS.addListener<RegisterClientReloadListenersEvent> { event ->
            for (listener in listeners) {
                event.registerReloadListener(listener)
            }
        }
    }
}