package me.muksc.tacztweaks.fabric

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.muksc.tacztweaks.core.registry.DeferredRegister
import me.muksc.tacztweaks.core.resource.IdentifiableResourceReloadListener
import me.muksc.tacztweaks.platform.Platform
import me.muksc.tacztweaks.platform.PlatformEvents
import me.muksc.tacztweaks.platform.PlatformNetwork
import me.muksc.tacztweaks.registry.ModRegistries
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.packs.PackType

object FabricPlatform : Platform {
    override val events: PlatformEvents get() = FabricPlatformEvents
    override val network: PlatformNetwork get() = FabricPlatformNetwork

    override fun registerDeferredRegistries(registries: List<DeferredRegister<*>>) = Unit // Not deferred in Fabric

    override fun registerDatapackRegistries(registries: List<ModRegistries.DataPackRegistry<*>>) {
        for (registry in registries) {
            registry.run { DynamicRegistries.register(key, codec) }
        }
    }

    override fun registerReloadListeners(listeners: List<IdentifiableResourceReloadListener>) {
        for (listener in listeners) {
            ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(listener)
        }
    }

    override fun registerCommands(commands: List<LiteralArgumentBuilder<CommandSourceStack>>) {
        CommandRegistrationCallback.EVENT.register { dispatcher, context, environment ->
            for (command in commands) {
                dispatcher.register(command)
            }
        }
    }
}