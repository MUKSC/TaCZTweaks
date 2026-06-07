package me.muksc.tacztweaks.network.message

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.network.CustomPacketPayload
import me.muksc.tacztweaks.core.network.CustomPacketPayloadType
import me.muksc.tacztweaks.core.codec.StreamCodec
import me.muksc.tacztweaks.mixininterface.feature.synced_slide.SlideDataHolder
import me.muksc.tacztweaks.mixininterop.shouldSlide
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

class ClientMessagePlayerShouldSlide(
    val shouldSlide: Boolean
) : CustomPacketPayload<ClientMessagePlayerShouldSlide> {
    companion object {
        val TYPE = CustomPacketPayloadType<ClientMessagePlayerShouldSlide>(
            TaCZTweaks.id("client_player_should_slide")
        )
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ClientMessagePlayerShouldSlide> =
            StreamCodec.of({ buf, packet ->
                buf.writeBoolean(packet.shouldSlide)
            }, { buf ->
                ClientMessagePlayerShouldSlide(buf.readBoolean())
            })

        fun handle(packet: ClientMessagePlayerShouldSlide, server: MinecraftServer, player: ServerPlayer?) = server.execute {
            if (player == null) return@execute
            SlideDataHolder.of(player).shouldSlide = packet.shouldSlide
        }
    }

    override fun self(): ClientMessagePlayerShouldSlide = this

    override fun type(): CustomPacketPayloadType<ClientMessagePlayerShouldSlide> = TYPE

    override fun codec(): StreamCodec<FriendlyByteBuf, ClientMessagePlayerShouldSlide> = STREAM_CODEC
}