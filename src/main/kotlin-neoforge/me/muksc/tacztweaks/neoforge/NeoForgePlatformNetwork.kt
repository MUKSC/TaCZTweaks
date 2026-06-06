package me.muksc.tacztweaks.neoforge

import me.muksc.tacztweaks.core.codec.StreamCodec
import me.muksc.tacztweaks.core.network.CustomPacketPayload
import me.muksc.tacztweaks.core.network.CustomPacketPayloadType
import me.muksc.tacztweaks.network.LoginIndexedMessage
import me.muksc.tacztweaks.platform.PlatformNetwork
import me.muksc.tacztweaks.platform.PlatformNetwork.ClientHandler
import me.muksc.tacztweaks.platform.PlatformNetwork.ServerHandler
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ConfigurationTask
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.ClientPayloadContext
import net.neoforged.neoforge.network.handling.IPayloadHandler
import net.neoforged.neoforge.network.handling.ServerPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import java.util.function.Consumer
import net.minecraft.network.protocol.common.custom.CustomPacketPayload as MCCustomPacketPayload

object NeoForgePlatformNetwork : PlatformNetwork {
    private val version = LOADING_CONTEXT.activeContainer.modInfo.version.run { "$majorVersion.$minorVersion" }
    private val playPackets = mutableListOf<PacketRegistration<*, RegistryFriendlyByteBuf>>()
    private val configurationPackets = mutableListOf<PacketRegistration<*, FriendlyByteBuf>>()
    init {
        MOD_BUS.addListener<RegisterPayloadHandlersEvent> { event ->
            val registrar = event.registrar(version)
            for (packet in playPackets) {
                registrar.registerPlay(packet)
            }
            for (packet in configurationPackets) {
                registrar.registerConfiguration(packet)
            }
        }
        MOD_BUS.addListener<RegisterConfigurationTasksEvent> { event ->
            for (packet in configurationPackets.filter { it.direction == EPacketDirection.TO_CLIENT }) {
                event.register(object : ICustomConfigurationTask {
                    val TYPE = ConfigurationTask.Type(packet.type.id)

                    override fun run(sender: Consumer<MCCustomPacketPayload>) {
                        sender.accept(packet.clazz.getConstructor().newInstance())
                        event.listener.finishCurrentTask(type())
                    }

                    override fun type(): ConfigurationTask.Type = TYPE
                })
            }
        }
    }

    override fun <T> sendC2S(packet: T) where T : CustomPacketPayload<T> {
        PacketDistributor.sendToServer(packet)
    }

    override fun <T : CustomPacketPayload<T>> sendS2C(server: MinecraftServer, packet: T) {
        PacketDistributor.sendToAllPlayers(packet)
    }

    override fun <T : CustomPacketPayload<T>> sendS2C(player: ServerPlayer, packet: T) {
        PacketDistributor.sendToPlayer(player, packet)
    }

    override fun <T : CustomPacketPayload<T>> registerC2S(
        clazz: Class<T>,
        type: CustomPacketPayloadType<T>,
        codec: StreamCodec<in RegistryFriendlyByteBuf, T>,
        handler: ServerHandler<T>
    ) {
        playPackets.add(PacketRegistration(
            direction = EPacketDirection.TO_SERVER,
            clazz = clazz,
            type = type,
            codec = codec,
            handler = { packet, context ->
                if (context !is ServerPayloadContext) return@PacketRegistration
                handler.handle(packet, context.player().server, context.player())
            }
        ))
    }

    override fun <T : CustomPacketPayload<T>> registerS2C(
        clazz: Class<T>,
        type: CustomPacketPayloadType<T>,
        codec: StreamCodec<in RegistryFriendlyByteBuf, T>,
        handler: ClientHandler<T>
    ) {
        playPackets.add(PacketRegistration(
            direction = EPacketDirection.TO_CLIENT,
            clazz = clazz,
            type = type,
            codec = codec,
            handler = { packet, context ->
                if (context !is ClientPayloadContext) return@PacketRegistration
                handler.handle(packet, Minecraft.getInstance())
            }
        ))
    }

    override fun <T : LoginIndexedMessage<T>> registerLoginS2C(
        clazz: Class<T>,
        type: CustomPacketPayloadType<T>,
        codec: StreamCodec<in FriendlyByteBuf, T>,
        handler: ClientHandler<T>
    ) {
        configurationPackets.add(PacketRegistration(
            direction = EPacketDirection.TO_CLIENT,
            clazz = clazz,
            type = type,
            codec = codec,
            handler = { packet, context ->
                if (context !is ClientPayloadContext) return@PacketRegistration
                handler.handle(packet, Minecraft.getInstance())
            }
        ))
    }

    data class PacketRegistration<T : CustomPacketPayload<T>, B : FriendlyByteBuf>(
        val direction: EPacketDirection,
        val clazz: Class<T>,
        val type: CustomPacketPayloadType<T>,
        val codec: StreamCodec<in B, T>,
        val handler: IPayloadHandler<T>
    )

    enum class EPacketDirection {
        TO_SERVER,
        TO_CLIENT
    }

    private fun <T : CustomPacketPayload<T>> PayloadRegistrar.registerPlay(packet: PacketRegistration<T, RegistryFriendlyByteBuf>) {
        when (packet.direction) {
            EPacketDirection.TO_SERVER -> playToServer(packet.type, packet.codec, packet.handler)
            EPacketDirection.TO_CLIENT -> playToClient(packet.type, packet.codec, packet.handler)
        }
    }

    private fun <T : CustomPacketPayload<T>> PayloadRegistrar.registerConfiguration(packet: PacketRegistration<T, FriendlyByteBuf>) {
        when (packet.direction) {
            EPacketDirection.TO_SERVER -> configurationToServer(packet.type, packet.codec, packet.handler)
            EPacketDirection.TO_CLIENT -> configurationToClient(packet.type, packet.codec, packet.handler)
        }
    }
}