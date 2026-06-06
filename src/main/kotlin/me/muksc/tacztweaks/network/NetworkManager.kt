package me.muksc.tacztweaks.network

import me.muksc.tacztweaks.config.ConfigManager
import me.muksc.tacztweaks.core.network.CustomPacketPayload
import me.muksc.tacztweaks.network.message.ClientMessageBroadcastSound
import me.muksc.tacztweaks.network.message.ClientMessagePlayerShouldSlide
import me.muksc.tacztweaks.network.message.ClientMessagePlayerUnload
import me.muksc.tacztweaks.network.message.ClientMessageSyncConfig
import me.muksc.tacztweaks.network.message.ServerMessageAirspaceSounds
import me.muksc.tacztweaks.network.message.ServerMessageBroadcastSound
import me.muksc.tacztweaks.network.message.ServerMessageSoundPhysicsRequired
import me.muksc.tacztweaks.network.message.ServerMessageSyncConfig
import me.muksc.tacztweaks.platform.PlatformNetwork
import me.muksc.tacztweaks.platform.PlatformNetwork.Companion.registerC2S
import me.muksc.tacztweaks.platform.PlatformNetwork.Companion.registerS2C
import me.muksc.tacztweaks.platform.PlatformNetwork.Companion.registerLoginS2C
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object NetworkManager {
    private lateinit var platform: PlatformNetwork

    fun initialize(platform: PlatformNetwork) {
        this.platform = platform
        platform.run {
            registerC2S(ClientMessageSyncConfig.TYPE, ClientMessageSyncConfig.STREAM_CODEC, ConfigManager::handle)
            registerS2C(ServerMessageSyncConfig.TYPE, ServerMessageSyncConfig.STREAM_CODEC, ConfigManager::handle)
            registerLoginS2C(ServerMessageSyncConfig.TYPE, ServerMessageSyncConfig.STREAM_CODEC, ConfigManager::handleLogin)

            registerC2S(ClientMessagePlayerShouldSlide.TYPE, ClientMessagePlayerShouldSlide.STREAM_CODEC, ClientMessagePlayerShouldSlide::handle)
            registerC2S(ClientMessagePlayerUnload.TYPE, ClientMessagePlayerUnload.STREAM_CODEC, ClientMessagePlayerUnload::handle)
            registerC2S(ClientMessageBroadcastSound.TYPE, ClientMessageBroadcastSound.STREAM_CODEC, ClientMessageBroadcastSound::handle)
            registerS2C(ServerMessageBroadcastSound.TYPE, ServerMessageBroadcastSound.STREAM_CODEC, ServerMessageBroadcastSound::handle)

            registerS2C(ServerMessageSoundPhysicsRequired.TYPE, ServerMessageSoundPhysicsRequired.STREAM_CODEC, ServerMessageSoundPhysicsRequired::handle)
            registerS2C(ServerMessageAirspaceSounds.TYPE, ServerMessageAirspaceSounds.STREAM_CODEC, ServerMessageAirspaceSounds::handle)
        }
    }

    @JvmStatic
    fun <T : CustomPacketPayload<T>> sendC2S(packet: T) = platform.sendC2S(packet)

    @JvmStatic
    fun <T : CustomPacketPayload<T>> sendS2C(server: MinecraftServer, packet: T) = platform.sendS2C(server, packet)

    @JvmStatic
    fun <T : CustomPacketPayload<T>> sendS2C(player: ServerPlayer, packet: T) = platform.sendS2C(player, packet)
}