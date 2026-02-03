package me.muksc.tacztweaks.network.message

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.network.CustomPacketPayload
import me.muksc.tacztweaks.network.NetworkHandler
import me.muksc.tacztweaks.network.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.ChunkPos

class ClientMessageBroadcastSound(
    val soundName: ResourceLocation,
    val volume: Float,
    val pitch: Float,
    val distance: Int
) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<ClientMessageBroadcastSound>(
            TaCZTweaks.id("client_broadcast_sound")
        )
        val STREAM_CODEC = StreamCodec.of(
            encoder = { packet, buf ->
                buf.writeResourceLocation(packet.soundName)
                buf.writeFloat(packet.volume)
                buf.writeFloat(packet.pitch)
                buf.writeInt(packet.distance)
            },
            decoder = { buf ->
                val soundName = buf.readResourceLocation()
                val volume = buf.readFloat()
                val pitch = buf.readFloat()
                val distance = buf.readInt()
                ClientMessageBroadcastSound(soundName, volume, pitch, distance)
            }
        )

        fun handle(packet: ClientMessageBroadcastSound, server: MinecraftServer, player: ServerPlayer?) {
            server.execute {
                if (player == null) return@execute
                val pos = player.blockPosition()
                player.serverLevel().chunkSource.chunkMap.getPlayers(ChunkPos(pos), false)
                    .filter { it.distanceToSqr(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()) < packet.distance * packet.distance }
                    .filter { it.id != player.id }
                    .forEach {
                        NetworkHandler.sendS2C(it, ServerMessageBroadcastSound(
                            player,
                            packet.soundName,
                            packet.volume,
                            packet.pitch,
                            packet.distance
                        ))
                    }
            }
        }
    }

    override fun type(): CustomPacketPayload.Type<ClientMessageBroadcastSound> = TYPE
}