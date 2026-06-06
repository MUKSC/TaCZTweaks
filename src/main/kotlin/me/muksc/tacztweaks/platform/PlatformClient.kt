package me.muksc.tacztweaks.platform

import me.muksc.tacztweaks.core.resource.IdentifiableResourceReloadListener
import net.minecraft.client.KeyMapping

interface PlatformClient {
    val events: PlatformEventsClient

    fun registerKeyMappings(keys: List<KeyMapping>)

    fun registerClientReloadListeners(listeners: List<IdentifiableResourceReloadListener>)
}