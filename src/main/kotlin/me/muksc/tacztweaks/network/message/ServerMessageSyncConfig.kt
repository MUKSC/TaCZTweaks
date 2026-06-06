package me.muksc.tacztweaks.network.message

import io.netty.buffer.Unpooled
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.core.network.CustomPacketPayloadType
import me.muksc.tacztweaks.core.codec.StreamCodec
import me.muksc.tacztweaks.network.LoginIndexedMessage
import net.minecraft.network.FriendlyByteBuf

class ServerMessageSyncConfig(
    val bytes: ByteArray
) : LoginIndexedMessage<ServerMessageSyncConfig>() {
    companion object {
        val TYPE = CustomPacketPayloadType<ServerMessageSyncConfig>(
            TaCZTweaks.id("server_sync_config")
        )
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ServerMessageSyncConfig> =
            StreamCodec.of({ buf, packet ->
                buf.writeByteArray(packet.bytes)
            }, { buf ->
                ServerMessageSyncConfig(buf.readByteArray())
            })
    }

    constructor() : this(Unpooled.buffer().apply(Config::encode).array())

    override fun self(): ServerMessageSyncConfig = this

    override fun type(): CustomPacketPayloadType<ServerMessageSyncConfig> = TYPE

    override fun codec(): StreamCodec<FriendlyByteBuf, ServerMessageSyncConfig> = STREAM_CODEC
}