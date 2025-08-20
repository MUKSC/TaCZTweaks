package me.muksc.tacztweaks.compat.soundphysics.network.message

import com.google.common.collect.Lists
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsCompat
import me.muksc.tacztweaks.network.CustomPacketPayload
import me.muksc.tacztweaks.network.StreamCodec
import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.game.ClientboundSoundPacket

class ServerMessageAirspaceSounds(
    val sounds: List<AirspaceSound>,
    val x: Double,
    val y: Double,
    val z: Double
) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<ServerMessageAirspaceSounds>(
            TaCZTweaks.id("airspace_sounds")
        )
        val STREAM_CODEC = StreamCodec.of(
            encoder = { packet, buf ->
                buf.writeCollection(packet.sounds) { buf, element ->
                    AirspaceSound.STREAM_CODEC.encode(element, buf)
                }
                buf.writeDouble(packet.x)
                buf.writeDouble(packet.y)
                buf.writeDouble(packet.z)
            },
            decoder = { buf ->
                val sounds = buf.readCollection(Lists::newArrayListWithCapacity, AirspaceSound.STREAM_CODEC::decode)
                val x = buf.readDouble()
                val y = buf.readDouble()
                val z = buf.readDouble()
                ServerMessageAirspaceSounds(sounds, x, y, z)
            }
        )

        fun handle(packet: ServerMessageAirspaceSounds, minecraft: Minecraft) {
            if (!SoundPhysicsCompat.isEnabled()) return
            minecraft.execute {
                SoundPhysicsCompat.play(minecraft, packet)
            }
        }
    }

    class AirspaceSound(
        val packet: ClientboundSoundPacket?,
        val minAirspace: Float,
        val maxAirspace: Float,
        val minOcclusion: Float,
        val maxOcclusion: Float,
        val minReflectivity: Float,
        val maxReflectivity: Float
    ) {
        companion object {
            val STREAM_CODEC = StreamCodec.of(
                encoder = { packet, buf ->
                    buf.writeNullable(packet.packet) { buf, value ->
                        value.write(buf)
                    }
                    buf.writeFloat(packet.minAirspace)
                    buf.writeFloat(packet.maxAirspace)
                    buf.writeFloat(packet.minOcclusion)
                    buf.writeFloat(packet.maxOcclusion)
                    buf.writeFloat(packet.minReflectivity)
                    buf.writeFloat(packet.maxReflectivity)
                },
                decoder = { buf ->
                    val packet = buf.readNullable(::ClientboundSoundPacket)
                    val minAirspace = buf.readFloat()
                    val maxAirspace = buf.readFloat()
                    val minOcclusion = buf.readFloat()
                    val maxOcclusion = buf.readFloat()
                    val minReflectivity = buf.readFloat()
                    val maxReflectivity = buf.readFloat()
                    AirspaceSound(packet, minAirspace, maxAirspace, minOcclusion, maxOcclusion, minReflectivity, maxReflectivity)
                }
            )
        }

        fun canPlayAtAirspace(airspace: Float): Boolean =
            airspace in minAirspace..maxAirspace

        fun canPlayAtOcclusion(occlusion: Double): Boolean =
            occlusion in minOcclusion..maxOcclusion

        fun canPlayAtReflectivity(reflectivity: Float): Boolean =
            reflectivity in minReflectivity..maxReflectivity
    }

    override fun type(): CustomPacketPayload.Type<ServerMessageAirspaceSounds> = TYPE
}