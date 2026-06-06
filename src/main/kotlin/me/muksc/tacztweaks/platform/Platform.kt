package me.muksc.tacztweaks.platform

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.muksc.tacztweaks.core.registry.DeferredRegister
import me.muksc.tacztweaks.core.resource.IdentifiableResourceReloadListener
import me.muksc.tacztweaks.registry.ModRegistries
import net.minecraft.commands.CommandSourceStack

interface Platform {
    val events: PlatformEvents
    val network: PlatformNetwork

    fun registerDeferredRegistries(registries: List<DeferredRegister<*>>)

    fun registerDatapackRegistries(registries: List<ModRegistries.DataPackRegistry<*>>)

    fun registerReloadListeners(listeners: List<IdentifiableResourceReloadListener>)

    fun registerCommands(commands: List<LiteralArgumentBuilder<CommandSourceStack>>)
}