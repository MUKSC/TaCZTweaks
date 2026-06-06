package me.muksc.tacztweaks.network.message

import io.netty.buffer.Unpooled
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.core.network.CustomPacketPayload
import me.muksc.tacztweaks.core.network.CustomPacketPayloadType
import me.muksc.tacztweaks.core.codec.StreamCodec
import net.minecraft.network.FriendlyByteBuf

class ClientMessageSyncConfig(
    val bytes: ByteArray
) : CustomPacketPayload<ClientMessageSyncConfig> {
    companion object {
        val TYPE = CustomPacketPayloadType<ClientMessageSyncConfig>(
            TaCZTweaks.id("client_sync_config")
        )
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ClientMessageSyncConfig> =
            StreamCodec.of({ buf, packet ->
                buf.writeByteArray(packet.bytes)
            }, { buf ->
                ClientMessageSyncConfig(buf.readByteArray())
            })
    }

    constructor() : this(Unpooled.buffer().apply(Config::encode).array())

    override fun self(): ClientMessageSyncConfig = this

    override fun type(): CustomPacketPayloadType<ClientMessageSyncConfig> = TYPE

    override fun codec(): StreamCodec<FriendlyByteBuf, ClientMessageSyncConfig> = STREAM_CODEC
}