package me.muksc.tacztweaks

import me.muksc.tacztweaks.client.input.BoltKey
import me.muksc.tacztweaks.client.input.ReduceSensitivityKey
import me.muksc.tacztweaks.client.input.TiltGunKey
import me.muksc.tacztweaks.client.input.UnloadKey
import me.muksc.tacztweaks.command.RootCommand
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.core.Identifier
import me.muksc.tacztweaks.event.EventsManager
import me.muksc.tacztweaks.feature.datapack.legacy.manager.BaseDataManager
import me.muksc.tacztweaks.network.NetworkManager
import me.muksc.tacztweaks.platform.Platform
import me.muksc.tacztweaks.platform.PlatformClient
import me.muksc.tacztweaks.registry.ModAttributes
import me.muksc.tacztweaks.registry.ModRegistries
import me.muksc.tacztweaks.registry.ModStatusEffects
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object TaCZTweaks {
    const val MOD_ID = "tacztweaks"
    const val MOD_NAME = "TaCZ Tweaks"
    val logger: Logger = LoggerFactory.getLogger(MOD_ID)

    fun id(path: String): ResourceLocation = Identifier(MOD_ID, path)

    fun translatable(key: String, vararg args: Any?): MutableComponent = Component.translatable("$MOD_ID.$key", *args)

    fun message(): MutableComponent = ComponentUtils.wrapInSquareBrackets(Component.literal(MOD_NAME)).append(" ")

    fun key(name: String): String = translatable("key.$name").string

    fun keyCategory(name: String): String = translatable("key.category.$name").string

    fun initialize(platform: Platform) {
        Config.initialize()
        EventsManager.initialize(platform.events)
        NetworkManager.initialize(platform.network)
        platform.registerDeferredRegistries(listOf(ModAttributes.REGISTRY, ModStatusEffects.REGISTRY))
        platform.registerDatapackRegistries(ModRegistries.REGISTRIES)
        platform.registerReloadListeners(BaseDataManager.ALL)
        platform.registerCommands(listOf(RootCommand.build()))
    }

    fun initializeClient(platform: PlatformClient) {
        EventsManager.initializeClient(platform.events)
        platform.registerKeyMappings(listOf(
            BoltKey.KEY,
            ReduceSensitivityKey.KEY,
            TiltGunKey.KEY,
            UnloadKey.KEY
        ))
    }
}