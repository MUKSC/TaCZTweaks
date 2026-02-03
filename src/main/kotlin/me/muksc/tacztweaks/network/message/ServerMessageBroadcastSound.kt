package me.muksc.tacztweaks.network.message

import com.tacz.guns.client.sound.SoundPlayManager
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.network.CustomPacketPayload
import me.muksc.tacztweaks.network.StreamCodec
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

class ServerMessageBroadcastSound(
    val entityId: Int,
    val soundName: ResourceLocation,
    val volume: Float,
    val pitch: Float,
    val distance: Int
) : CustomPacketPayload {
    constructor(entity: Entity, soundName: ResourceLocation, volume: Float, pitch: Float, distance: Int) : this(entity.id, soundName, volume, pitch, distance)

    companion object {
        val TYPE = CustomPacketPayload.Type<ServerMessageBroadcastSound>(
            TaCZTweaks.id("server_broadcast_sound")
        )
        val STREAM_CODEC = StreamCodec.of(
            encoder = { packet, buf ->
                buf.writeInt(packet.entityId)
                buf.writeResourceLocation(packet.soundName)
                buf.writeFloat(packet.volume)
                buf.writeFloat(packet.pitch)
                buf.writeInt(packet.distance)
            },
            decoder = { buf ->
                val entityId = buf.readInt()
                val soundName = buf.readResourceLocation()
                val volume = buf.readFloat()
                val pitch = buf.readFloat()
                val distance = buf.readInt()
                ServerMessageBroadcastSound(entityId, soundName, volume, pitch, distance)
            }
        )

        fun handle(packet: ServerMessageBroadcastSound, minecraft: Minecraft) {
            val entity = minecraft.level?.getEntity(packet.entityId) ?: return
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

    override fun type(): CustomPacketPayload.Type<ServerMessageBroadcastSound> = TYPE
}