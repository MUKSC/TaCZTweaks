package me.muksc.tacztweaks.network.message

import io.netty.buffer.Unpooled
import me.muksc.tacztweaks.Config
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.ConfigManager
import me.muksc.tacztweaks.config.ESyncDirection
import me.muksc.tacztweaks.network.CustomPacketPayload
import me.muksc.tacztweaks.network.NetworkHandler
import me.muksc.tacztweaks.network.StreamCodec
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

class ClientMessageSyncConfig(
    val buf: FriendlyByteBuf
) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<ClientMessageSyncConfig>(
            TaCZTweaks.id("client_sync_config")
        )
        val STREAM_CODEC = StreamCodec.of(
            encoder = { packet, buf ->
                buf.writeInt(packet.buf.writerIndex())
                buf.writeBytes(packet.buf)
            },
            decoder = { buf ->
                val size = buf.readInt()
                val data = FriendlyByteBuf(buf.readBytes(size))
                ClientMessageSyncConfig(data)
            }
        )

        @Suppress("UnstableApiUsage")
        fun handle(packet: ClientMessageSyncConfig, server: MinecraftServer, player: ServerPlayer?) {
            if (player == null) return
            if (!ConfigManager.canUpdateServerConfig(player)) return
            Config.decode(packet.buf)
            Config.sync(ESyncDirection.CLIENT_TO_SERVER)
            Config.saveToFile()

            NetworkHandler.sendS2C(ServerMessageSyncConfig())
        }
    }

    constructor() : this(FriendlyByteBuf(Unpooled.buffer()).apply {
        Config.encode(this)
    })

    override fun type(): CustomPacketPayload.Type<ClientMessageSyncConfig> = TYPE
}
