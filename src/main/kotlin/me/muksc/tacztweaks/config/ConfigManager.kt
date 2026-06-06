package me.muksc.tacztweaks.config

import com.tacz.guns.resource.modifier.AttachmentPropertyManager
import io.netty.buffer.Unpooled
import me.muksc.tacztweaks.config.sync.ESyncDirection
import me.muksc.tacztweaks.network.NetworkManager
import me.muksc.tacztweaks.network.message.ClientMessageSyncConfig
import me.muksc.tacztweaks.network.message.ServerMessageSyncConfig
import net.minecraft.client.Minecraft
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

object ConfigManager {
    var syncedWithServer = false
        private set

    fun saveAndSync() {
        val minecraft = Minecraft.getInstance()
        if (minecraft.level == null) { // Not in-game
            Config.sync(ESyncDirection.NONE)
        } else if (minecraft.singleplayerServer != null) { // Singleplayer (including LAN)
            Config.sync(ESyncDirection.NONE)
            NetworkManager.sendC2S(ClientMessageSyncConfig())
        } else if (syncedWithServer && canUpdateServerConfig()) { // Server with permissions
            NetworkManager.sendC2S(ClientMessageSyncConfig())
        }
        Config.runAsSaving(Config::saveToFile)
    }

    fun canUpdateServerConfig(player: Player) =
        player.hasPermissions(2)

    fun canUpdateServerConfig(): Boolean {
        return canUpdateServerConfig(Minecraft.getInstance().player ?: return false)
    }

    fun handle(packet: ClientMessageSyncConfig, server: MinecraftServer, player: ServerPlayer?) = server.execute {
        if (player == null || (!server.isSingleplayer && !canUpdateServerConfig(player))) return@execute
        if (!server.isSingleplayer) { /** Singleplayer (already handled in [saveAndSync]) */
            Config.decode(Unpooled.wrappedBuffer(packet.bytes))
            Config.sync(ESyncDirection.CLIENT_TO_SERVER)
            Config.saveToFile()
        }
        AttachmentPropertyManager.postChangeEvent(player, player.mainHandItem)
        NetworkManager.sendS2C(server, ServerMessageSyncConfig())
    }

    fun handle(packet: ServerMessageSyncConfig, minecraft: Minecraft) = minecraft.execute {
        if (minecraft.singleplayerServer == null) { /** Singleplayer (already handled in [saveAndSync]) */
            Config.decode(Unpooled.wrappedBuffer(packet.bytes))
            Config.sync(ESyncDirection.SERVER_TO_CLIENT)
        }
        minecraft.player?.run {
            AttachmentPropertyManager.postChangeEvent(this, mainHandItem)
        }
    }

    fun handleLogin(packet: ServerMessageSyncConfig, minecraft: Minecraft) = minecraft.execute {
        if (minecraft.isSingleplayer) return@execute
        syncedWithServer = true
        handle(packet, minecraft)
    }

    fun onLoggingOut() {
        syncedWithServer = false
        Config.sync(ESyncDirection.RESET)
    }
}