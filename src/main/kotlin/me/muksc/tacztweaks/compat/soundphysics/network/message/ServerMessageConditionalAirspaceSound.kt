package me.muksc.tacztweaks.compat.soundphysics.network.message

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsConditionalSoundInstance
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsCompat
import me.muksc.tacztweaks.network.CustomPacketPayload
import me.muksc.tacztweaks.network.StreamCodec
import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.server.commands.PlaySoundCommand
import net.minecraft.util.RandomSource

class ServerMessageConditionalAirspaceSound(
    val packet: ClientboundSoundPacket,
    val minAirspace: Float,
    val maxAirspace: Float,
    val minOcclusion: Float,
    val maxOcclusion: Float,
    val minReflectivity: Float,
    val maxReflectivity: Float
) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<ServerMessageConditionalAirspaceSound>(
            TaCZTweaks.id("conditional_airspace_sound")
        )
        val STREAM_CODEC = StreamCodec.Companion.of(
            encoder = { packet, buf ->
                packet.packet.write(buf)
                buf.writeFloat(packet.minAirspace)
                buf.writeFloat(packet.maxAirspace)
                buf.writeFloat(packet.minOcclusion)
                buf.writeFloat(packet.maxOcclusion)
                buf.writeFloat(packet.minReflectivity)
                buf.writeFloat(packet.maxReflectivity)
            },
            decoder = { buf ->
                val packet = ClientboundSoundPacket(buf)
                val minAirspace = buf.readFloat()
                val maxAirspace = buf.readFloat()
                val minOcclusion = buf.readFloat()
                val maxOcclusion = buf.readFloat()
                val minReflectivity = buf.readFloat()
                val maxReflectivity = buf.readFloat()
                ServerMessageConditionalAirspaceSound(packet, minAirspace, maxAirspace, minOcclusion, maxOcclusion, minReflectivity, maxReflectivity)
            }
        )

        fun handle(packet: ServerMessageConditionalAirspaceSound, minecraft: Minecraft) {
            if (!SoundPhysicsCompat.isEnabled()) return
            minecraft.execute {
                packet.packet.run {
                    minecraft.soundManager.play(
                        SoundPhysicsConditionalSoundInstance(
                            sound.get(),
                            source,
                            volume,
                            pitch,
                            RandomSource.create(seed),
                            x,
                            y,
                            z,
                            packet.minAirspace,
                            packet.maxAirspace,
                            packet.minOcclusion,
                            packet.maxOcclusion,
                            packet.minReflectivity,
                            packet.maxReflectivity
                        )
                    )
                }
            }
        }
    }

    override fun type(): CustomPacketPayload.Type<ServerMessageConditionalAirspaceSound> = TYPE
}