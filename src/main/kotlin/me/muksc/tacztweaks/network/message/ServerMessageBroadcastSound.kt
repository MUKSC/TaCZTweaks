package me.muksc.tacztweaks.network.message

import com.tacz.guns.client.resource.pojo.display.SoundData
import com.tacz.guns.config.client.SoundConfig
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.network.CustomPacketPayload
import me.muksc.tacztweaks.core.network.CustomPacketPayloadType
import me.muksc.tacztweaks.core.codec.StreamCodec
import me.muksc.tacztweaks.core.extension.readSoundData
import me.muksc.tacztweaks.core.extension.writeSoundData
import me.muksc.tacztweaks.mixin.accessor.SoundPlayManagerAccessor
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf

class ServerMessageBroadcastSound(
    val entityId: Int,
    val sound: SoundData,
    val volume: Float,
    val pitch: Float,
    val distance: Int
) : CustomPacketPayload<ServerMessageBroadcastSound> {
    companion object {
        val TYPE = CustomPacketPayloadType<ServerMessageBroadcastSound>(
            TaCZTweaks.id("server_broadcast_sound")
        )
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ServerMessageBroadcastSound> =
            StreamCodec.of({ buf, packet ->
                buf.writeInt(packet.entityId)
                buf.writeSoundData(packet.sound)
                buf.writeFloat(packet.volume)
                buf.writeFloat(packet.pitch)
                buf.writeInt(packet.distance)
            }, { buf ->
                val entityId = buf.readInt()
                val sound = buf.readSoundData()
                val volume = buf.readFloat()
                val pitch = buf.readFloat()
                val distance = buf.readInt()
                ServerMessageBroadcastSound(entityId, sound, volume, pitch, distance)
            })

        fun handle(packet: ServerMessageBroadcastSound, minecraft: Minecraft) = minecraft.execute {
            val entity = minecraft.level?.getEntity(packet.entityId) ?: return@execute
            SoundPlayManagerAccessor.`tacztweaks$invokePlayClientSound`(
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

    override fun self(): ServerMessageBroadcastSound = this

    override fun type(): CustomPacketPayloadType<ServerMessageBroadcastSound> = TYPE

    override fun codec(): StreamCodec<FriendlyByteBuf, ServerMessageBroadcastSound> = STREAM_CODEC
}