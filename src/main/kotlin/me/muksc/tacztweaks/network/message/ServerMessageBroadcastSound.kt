package me.muksc.tacztweaks.network.message

import com.tacz.guns.client.resource.pojo.display.SoundData
import com.tacz.guns.config.client.SoundConfig
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.mixin.accessor.SoundDataAccessor
import me.muksc.tacztweaks.mixin.accessor.SoundPlayManagerAccessor
import me.muksc.tacztweaks.network.CustomPacketPayload
import me.muksc.tacztweaks.network.StreamCodec
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.Entity
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

class ServerMessageBroadcastSound(
    val entityId: Int,
    val sound: SoundData,
    val volume: Float,
    val pitch: Float,
    val distance: Int
) : CustomPacketPayload {
    constructor(entity: Entity, sound: SoundData, volume: Float, pitch: Float, distance: Int) : this(entity.id, sound, volume, pitch, distance)

    companion object {
        val TYPE = CustomPacketPayload.Type<ServerMessageBroadcastSound>(
            TaCZTweaks.id("server_broadcast_sound")
        )
        val STREAM_CODEC = StreamCodec.of(
            encoder = { packet, buf ->
                buf.writeInt(packet.entityId)
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
                val entityId = buf.readInt()
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
                ServerMessageBroadcastSound(entityId, sound, volume, pitch, distance)
            }
        )

        fun handle(packet: ServerMessageBroadcastSound, minecraft: Minecraft) {
            val entity = minecraft.level?.getEntity(packet.entityId) ?: return
            minecraft.execute {
                SoundPlayManagerAccessor.invokePlayClientSound(
                    entity,
                    packet.sound,
                    packet.volume,
                    packet.pitch,
                    packet.distance,
                    true,
                    SoundConfig.DEFAULT_SOUND_CONCURRENCY_LIMIT.get(),
                    true,
                    true
                )
            }
        }
    }

    override fun type(): CustomPacketPayload.Type<ServerMessageBroadcastSound> = TYPE
}