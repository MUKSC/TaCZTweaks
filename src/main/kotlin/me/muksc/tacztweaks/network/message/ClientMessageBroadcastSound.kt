package me.muksc.tacztweaks.network.message

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.network.CustomPacketPayload
import me.muksc.tacztweaks.core.network.CustomPacketPayloadType
import me.muksc.tacztweaks.core.codec.StreamCodec
import me.muksc.tacztweaks.network.NetworkManager
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.ChunkPos

class ClientMessageBroadcastSound(
    val soundName: ResourceLocation,
    val volume: Float,
    val pitch: Float,
    val distance: Int
) : CustomPacketPayload<ClientMessageBroadcastSound> {
    companion object {
        val TYPE = CustomPacketPayloadType<ClientMessageBroadcastSound>(
            TaCZTweaks.id("client_broadcast_sound")
        )
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ClientMessageBroadcastSound> =
            StreamCodec.of({ buf, packet ->
                buf.writeResourceLocation(packet.soundName)
                buf.writeFloat(packet.volume)
                buf.writeFloat(packet.pitch)
                buf.writeInt(packet.distance)
            }, { buf ->
                val soundName = buf.readResourceLocation()
                val volume = buf.readFloat()
                val pitch = buf.readFloat()
                val distance = buf.readInt()
                ClientMessageBroadcastSound(soundName, volume, pitch, distance)
            })

        fun handle(packet: ClientMessageBroadcastSound, server: MinecraftServer, player: ServerPlayer?) {
            if (player == null) return
            val blockPos = player.blockPosition()
            player.serverLevel().chunkSource.chunkMap.getPlayers(ChunkPos(blockPos), false)
                .filter {
                    if (it.id == player.id) return@filter false
                    val distance = it.distanceToSqr(player.x, player.y, player.z)
                    distance < packet.distance * packet.distance
                }
                .forEach {
                    NetworkManager.sendS2C(it, ServerMessageBroadcastSound(
                        player.id,
                        packet.soundName,
                        packet.volume,
                        packet.pitch,
                        packet.distance
                    ))
                }
        }
    }

    override fun self(): ClientMessageBroadcastSound = this

    override fun type(): CustomPacketPayloadType<ClientMessageBroadcastSound> = TYPE

    override fun codec(): StreamCodec<FriendlyByteBuf, ClientMessageBroadcastSound> = STREAM_CODEC
}