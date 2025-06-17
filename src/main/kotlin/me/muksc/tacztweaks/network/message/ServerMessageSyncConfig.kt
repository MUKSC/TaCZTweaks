package me.muksc.tacztweaks.network.message

import io.netty.buffer.Unpooled
import me.muksc.tacztweaks.Config
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.ConfigManager
import me.muksc.tacztweaks.config.ESyncDirection
import me.muksc.tacztweaks.network.CustomPacketPayload
import me.muksc.tacztweaks.network.StreamCodec
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf

class ServerMessageSyncConfig(
    val buf: FriendlyByteBuf
) : LoginIndexedMessage(), CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<ServerMessageSyncConfig>(
            TaCZTweaks.id("server_sync_config")
        )
        val STREAM_CODEC = StreamCodec.of(
            encoder = { packet, buf ->
                buf.writeInt(packet.buf.writerIndex())
                buf.writeBytes(packet.buf)
            },
            decoder = { buf ->
                val size = buf.readInt()
                val data = FriendlyByteBuf(buf.readBytes(size))
                ServerMessageSyncConfig(data)
            }
        )

        fun handle(packet: ServerMessageSyncConfig, minecraft: Minecraft) {
            Config.decode(packet.buf)
            Config.sync(ESyncDirection.SERVER_TO_CLIENT)
        }

        fun handleLogin(packet: ServerMessageSyncConfig, minecraft: Minecraft) {
            if (minecraft.isSingleplayer) return
            ConfigManager.syncedWithServer = true
            handle(packet, minecraft)
        }
    }

    constructor() : this(FriendlyByteBuf(Unpooled.buffer()).apply {
        Config.encode(this)
    })

    override fun type(): CustomPacketPayload.Type<ServerMessageSyncConfig> = TYPE
}