package me.muksc.tacztweaks.network.message

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.codec.StreamCodec
import me.muksc.tacztweaks.core.network.CustomPacketPayload
import me.muksc.tacztweaks.core.network.CustomPacketPayloadType
import me.muksc.tacztweaks.feature.datapack.legacy.manager.BulletSoundsManager
import me.muksc.tacztweaks.feature.datapack.legacy.manager.BulletSoundsManager.infoDebug
import me.muksc.tacztweaks.feature.sound_physics_evaluate.SoundPhysicsManager
import net.minecraft.client.Minecraft
import net.minecraft.core.NonNullList
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.game.ClientboundSoundPacket

//? if >=1.20.5 {
/*import net.minecraft.network.RegistryFriendlyByteBuf
import me.muksc.tacztweaks.core.codec.readCollection
import me.muksc.tacztweaks.core.codec.writeCollection
*///?} else {
import net.minecraft.network.FriendlyByteBuf as RegistryFriendlyByteBuf
//?}

class ServerMessageAirspaceSounds(
    val sounds: List<AirspaceSound>,
    val x: Double,
    val y: Double,
    val z: Double
) : CustomPacketPayload<ServerMessageAirspaceSounds> {
    companion object {
        val TYPE = CustomPacketPayloadType<ServerMessageAirspaceSounds>(
            TaCZTweaks.id("server_airspace_sounds")
        )
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ServerMessageAirspaceSounds> =
            StreamCodec.of({ buf, value ->
                buf.writeCollection(value.sounds, AirspaceSound.STREAM_CODEC::encode)
                buf.writeDouble(value.x)
                buf.writeDouble(value.y)
                buf.writeDouble(value.z)

            }, { buf ->
                val sounds = buf.readCollection(NonNullList<*>::createWithCapacity, AirspaceSound.STREAM_CODEC::decode)
                val x = buf.readDouble()
                val y = buf.readDouble()
                val z = buf.readDouble()
                ServerMessageAirspaceSounds(sounds, x, y, z)
            })

        fun handle(packet: ServerMessageAirspaceSounds, minecraft: Minecraft) = minecraft.execute {
            SoundPhysicsManager.play(minecraft, packet.x, packet.y, packet.z) { result ->
                BulletSoundsManager.logger.infoDebug("airspace: ${result.airspace}, occlusion: ${result.occlusionAccumulation}, reflectivity: ${result.reflectivity}")
                val sound = packet.sounds.firstOrNull {
                    result.airspace in it.airspace
                        && result.occlusionAccumulation in it.occlusionAccumulation
                        && result.reflectivity in it.reflectivity
                } ?: return@play
                minecraft.execute {
                    for (packet in sound.packets) {
                        minecraft.connection?.handleSoundEvent(packet)
                    }
                }
            }
        }
    }

    override fun self(): ServerMessageAirspaceSounds = this

    override fun type(): CustomPacketPayloadType<ServerMessageAirspaceSounds> = TYPE

    override fun codec(): StreamCodec<RegistryFriendlyByteBuf, ServerMessageAirspaceSounds> = STREAM_CODEC

    class AirspaceSound(
        val packets: List<ClientboundSoundPacket>,
        val airspace: ClosedFloatingPointRange<Double>,
        val occlusionAccumulation: ClosedFloatingPointRange<Double>,
        val reflectivity: ClosedFloatingPointRange<Double>
    ) {
        companion object {
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, AirspaceSound> = StreamCodec.of({ buf, value ->
                //? if >=1.20.5 {
                /*buf.writeCollection(value.packets, ClientboundSoundPacket.STREAM_CODEC::encode)
                *///?} else {
                buf.writeCollection(value.packets) { buf, element -> element.write(buf) }
                //?}
                buf.writeDoubleRange(value.airspace)
                buf.writeDoubleRange(value.occlusionAccumulation)
                buf.writeDoubleRange(value.reflectivity)
            }, { buf ->
                //? if >=1.20.5 {
                /*val packets = buf.readCollection<RegistryFriendlyByteBuf, ClientboundSoundPacket, NonNullList<ClientboundSoundPacket>>(NonNullList<*>::createWithCapacity, ClientboundSoundPacket.STREAM_CODEC::decode)
                *///?} else {
                val packets = buf.readCollection(NonNullList<*>::createWithCapacity, ::ClientboundSoundPacket)
                //?}
                val airspace = buf.readDoubleRange()
                val occlusionAccumulation = buf.readDoubleRange()
                val reflectivity = buf.readDoubleRange()
                AirspaceSound(packets, airspace, occlusionAccumulation, reflectivity)
            })

            private fun FriendlyByteBuf.writeDoubleRange(range: ClosedFloatingPointRange<Double>) {
                writeDouble(range.start)
                writeDouble(range.endInclusive)
            }

            private fun FriendlyByteBuf.readDoubleRange(): ClosedFloatingPointRange<Double> {
                val start = readDouble()
                val endInclusive = readDouble()
                return start..endInclusive
            }
        }
    }
}