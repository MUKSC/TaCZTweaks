package me.muksc.tacztweaks.network.message

import com.tacz.guns.client.resource.pojo.display.SoundData
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.mixin.accessor.SoundDataAccessor
import me.muksc.tacztweaks.network.CustomPacketPayload
import me.muksc.tacztweaks.network.NetworkHandler
import me.muksc.tacztweaks.network.StreamCodec
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.ChunkPos
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

class ClientMessageBroadcastSound(
    val sound: SoundData,
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
                packet.sound.run {
                    buf.writeResourceLocation(sound)
                    buf.writeOptional(Optional.ofNullable(volume), FriendlyByteBuf::writeFloat)
                    buf.writeOptional(Optional.ofNullable(pitch), FriendlyByteBuf::writeFloat)
                    buf.writeFloat(volumeRandomRange)
                    buf.writeFloat(pitchRandomRange)
                }
                buf.writeFloat(packet.volume)
                buf.writeFloat(packet.pitch)
                buf.writeInt(packet.distance)
            },
            decoder = { buf ->
                val sound = run {
                    val sound = buf.readResourceLocation()
                    val volume = buf.readOptional(FriendlyByteBuf::readFloat).getOrNull()
                    val pitch = buf.readOptional(FriendlyByteBuf::readFloat).getOrNull()
                    val volumeRandomRange = buf.readFloat()
                    val pitchRandomRange = buf.readFloat()
                    SoundDataAccessor.newSoundData(sound, volume, pitch, volumeRandomRange, pitchRandomRange)
                }
                val volume = buf.readFloat()
                val pitch = buf.readFloat()
                val distance = buf.readInt()
                ClientMessageBroadcastSound(sound, volume, pitch, distance)
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
                            packet.sound,
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