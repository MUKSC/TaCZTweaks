package me.muksc.tacztweaks.forge

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.muksc.tacztweaks.core.registry.DeferredRegister
import me.muksc.tacztweaks.core.resource.IdentifiableResourceReloadListener
import me.muksc.tacztweaks.platform.Platform
import me.muksc.tacztweaks.platform.PlatformEvents
import me.muksc.tacztweaks.platform.PlatformNetwork
import me.muksc.tacztweaks.registry.ModRegistries
import net.minecraft.commands.CommandSourceStack
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.registries.DataPackRegistryEvent
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

object ForgePlatform : Platform {
    override val events: PlatformEvents get() = ForgePlatformEvents
    override val network: PlatformNetwork get() = ForgePlatformNetwork

    override fun registerDeferredRegistries(registries: List<DeferredRegister<*>>) {
        for (registry in registries) {
            registry.register(MOD_BUS)
        }
    }

    override fun registerDatapackRegistries(registries: List<ModRegistries.DataPackRegistry<*>>) {
        MOD_BUS.addListener<DataPackRegistryEvent.NewRegistry> { event ->
            for (registry in registries) {
                registry.run { event.dataPackRegistry(key, codec) }
            }
        }
    }

    override fun registerReloadListeners(listeners: List<IdentifiableResourceReloadListener>) {
        FORGE_BUS.addListener<AddReloadListenerEvent> { event ->
            for (listener in listeners) {
                event.addListener(listener)
            }
        }
    }

    override fun registerCommands(commands: List<LiteralArgumentBuilder<CommandSourceStack>>) {
        FORGE_BUS.addListener<RegisterCommandsEvent> { event ->
            for (command in commands) {
                event.dispatcher.register(command)
            }
        }
    }
}