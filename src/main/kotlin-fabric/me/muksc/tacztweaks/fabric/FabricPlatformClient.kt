package me.muksc.tacztweaks.fabric

import me.muksc.tacztweaks.core.resource.IdentifiableResourceReloadListener
import me.muksc.tacztweaks.platform.PlatformClient
import me.muksc.tacztweaks.platform.PlatformEventsClient
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.client.KeyMapping
import net.minecraft.server.packs.PackType

object FabricPlatformClient : PlatformClient {
    override val events: PlatformEventsClient get() = FabricPlatformEventsClient

    override fun registerKeyMappings(keys: List<KeyMapping>) {
        for (key in keys) {
            KeyBindingHelper.registerKeyBinding(key)
        }
    }

    override fun registerClientReloadListeners(listeners: List<IdentifiableResourceReloadListener>) {
        for (listener in listeners) {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(listener)
        }
    }
}