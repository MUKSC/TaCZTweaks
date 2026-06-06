package me.muksc.tacztweaks.forge

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.network.CustomPacketPayload
import me.muksc.tacztweaks.core.network.CustomPacketPayloadType
import me.muksc.tacztweaks.core.codec.StreamCodec
import me.muksc.tacztweaks.core.reverse
import me.muksc.tacztweaks.network.LoginIndexedMessage
import me.muksc.tacztweaks.platform.PlatformNetwork
import me.muksc.tacztweaks.platform.PlatformNetwork.ClientHandler
import me.muksc.tacztweaks.platform.PlatformNetwork.ServerHandler
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.BiConsumer

object ForgePlatformNetwork : PlatformNetwork {
    private val version = LOADING_CONTEXT.activeContainer.modInfo.version.run { "$majorVersion.$minorVersion" }
    private val channel = NetworkRegistry.ChannelBuilder.named(TaCZTweaks.id("channel"))
        .networkProtocolVersion(::version)
        .clientAcceptedVersions(version::equals)
        .serverAcceptedVersions(version::equals)
        .simpleChannel()
    private val handshake = NetworkRegistry.ChannelBuilder.named(TaCZTweaks.id("handshake"))
        .networkProtocolVersion(::version)
        .clientAcceptedVersions(version::equals)
        .serverAcceptedVersions(version::equals)
        .simpleChannel()
    private val counter = AtomicInteger()
    private val handshakeCounter = AtomicInteger()

    override fun <T : CustomPacketPayload<T>> sendC2S(packet: T) {
        channel.sendToServer(packet)
    }

    override fun <T : CustomPacketPayload<T>> sendS2C(server: MinecraftServer, packet: T) {
        channel.send(PacketDistributor.ALL.noArg(), packet)
    }

    override fun <T : CustomPacketPayload<T>> sendS2C(player: ServerPlayer, packet: T) {
        channel.send(PacketDistributor.PLAYER.with { player }, packet)
    }

    @Suppress("INFERRED_INVISIBLE_RETURN_TYPE_WARNING")
    override fun <T : CustomPacketPayload<T>> registerC2S(
        clazz: Class<T>,
        type: CustomPacketPayloadType<T>,
        codec: StreamCodec<in FriendlyByteBuf, T>,
        handler: ServerHandler<T>
    ){
        channel.registerMessage(counter.getAndIncrement(), clazz, codec::encode.reverse(), codec::decode, { packet, supplier ->
            val context = supplier.get()
            val player = context.sender ?: return@registerMessage
            handler.handle(packet, player.server, player)
            context.packetHandled = true
        }, Optional.of(NetworkDirection.PLAY_TO_SERVER))
    }

    @Suppress("INFERRED_INVISIBLE_RETURN_TYPE_WARNING")
    override fun <T : CustomPacketPayload<T>> registerS2C(
        clazz: Class<T>,
        type: CustomPacketPayloadType<T>,
        codec: StreamCodec<in FriendlyByteBuf, T>,
        handler: ClientHandler<T>
    ) {
        channel.registerMessage(counter.getAndIncrement(), clazz, codec::encode.reverse(), codec::decode, { packet, supplier ->
            val context = supplier.get()
            handler.handle(packet, Minecraft.getInstance())
            context.packetHandled = true
        }, Optional.of(NetworkDirection.PLAY_TO_CLIENT))
    }

    override fun <T : LoginIndexedMessage<T>> registerLoginS2C(
        clazz: Class<T>,
        type: CustomPacketPayloadType<T>,
        codec: StreamCodec<in FriendlyByteBuf, T>,
        handler: ClientHandler<T>
    ) {
        handshake.messageBuilder(clazz, handshakeCounter.getAndIncrement(), NetworkDirection.LOGIN_TO_CLIENT)
            .loginIndex(LoginIndexedMessage<*>::loginIndex, LoginIndexedMessage<*>::loginIndex::set)
            .encoder(codec::encode.reverse())
            .decoder(codec::decode)
            .consumerNetworkThread(BiConsumer { packet, supplier ->
                val context = supplier.get()
                if (context.direction != NetworkDirection.LOGIN_TO_CLIENT) return@BiConsumer
                handler.handle(packet, Minecraft.getInstance())
                context.packetHandled = true
            })
            .noResponse()
            .markAsLoginPacket()
            .add()
    }
}