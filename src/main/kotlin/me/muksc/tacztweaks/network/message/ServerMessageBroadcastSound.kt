package me.muksc.tacztweaks.network.message

import com.tacz.guns.client.sound.SoundPlayManager
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.network.CustomPacketPayload
import me.muksc.tacztweaks.core.network.CustomPacketPayloadType
import me.muksc.tacztweaks.core.codec.StreamCodec
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation

class ServerMessageBroadcastSound(
    val entityId: Int,
    val soundName: ResourceLocation,
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
                buf.writeResourceLocation(packet.soundName)
                buf.writeFloat(packet.volume)
                buf.writeFloat(packet.pitch)
                buf.writeInt(packet.distance)
            }, { buf ->
                val entityId = buf.readInt()
                val soundName = buf.readResourceLocation()
                val volume = buf.readFloat()
                val pitch = buf.readFloat()
                val distance = buf.readInt()
                ServerMessageBroadcastSound(entityId, soundName, volume, pitch, distance)
            })

        fun handle(packet: ServerMessageBroadcastSound, minecraft: Minecraft) = minecraft.execute {
            val entity = minecraft.level?.getEntity(packet.entityId) ?: return@execute
            SoundPlayManager.playClientSound(
                entity,
                packet.soundName,
                packet.volume,
                packet.pitch,
                packet.distance,
                true
            )
        }
    }

    override fun self(): ServerMessageBroadcastSound = this

    override fun type(): CustomPacketPayloadType<ServerMessageBroadcastSound> = TYPE

    override fun codec(): StreamCodec<FriendlyByteBuf, ServerMessageBroadcastSound> = STREAM_CODEC
}