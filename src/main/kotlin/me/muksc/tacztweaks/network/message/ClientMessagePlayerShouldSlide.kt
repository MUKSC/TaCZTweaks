package me.muksc.tacztweaks.network.message

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.mixininterface.gun.SlideDataHolder
import me.muksc.tacztweaks.network.CustomPacketPayload
import me.muksc.tacztweaks.network.StreamCodec
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

class ClientMessagePlayerShouldSlide(
    val shouldSlide: Boolean
) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<ClientMessagePlayerShouldSlide>(
            TaCZTweaks.id("client_player_should_slide")
        )
        val STREAM_CODEC = StreamCodec.of(
            encoder = { packet, buf -> buf.writeBoolean(packet.shouldSlide) },
            decoder = { buf -> ClientMessagePlayerShouldSlide(buf.readBoolean()) }
        )

        fun handle(packet: ClientMessagePlayerShouldSlide, server: MinecraftServer, player: ServerPlayer?) {
            (player as SlideDataHolder).`tacztweaks$setShouldSlide`(packet.shouldSlide)
        }
    }

    override fun type(): CustomPacketPayload.Type<ClientMessagePlayerShouldSlide> = TYPE
}