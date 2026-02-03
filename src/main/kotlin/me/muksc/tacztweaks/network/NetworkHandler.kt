package me.muksc.tacztweaks.network

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.network.message.*
import net.minecraft.client.Minecraft
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.server.ServerAboutToStartEvent
import net.minecraftforge.event.server.ServerStoppedEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.BiConsumer

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = TaCZTweaks.MOD_ID)
object NetworkHandler {
    private var server: MinecraftServer? = null
    private val version = TaCZTweaks.container.modInfo.version.run { "$majorVersion.$minorVersion" }
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

    @JvmStatic
    @SubscribeEvent
    fun onServerAboutToStart(event: ServerAboutToStartEvent) {
        server = event.server
    }

    @JvmStatic
    @SubscribeEvent
    fun onServerStopped(event: ServerStoppedEvent) {
        server = null
    }

    fun register() {
        registerC2S(ClientMessageBroadcastSound.TYPE, ClientMessageBroadcastSound.STREAM_CODEC, ClientMessageBroadcastSound::handle)
        registerC2S(ClientMessagePlayerShouldSlide.TYPE, ClientMessagePlayerShouldSlide.STREAM_CODEC, ClientMessagePlayerShouldSlide::handle)
        registerC2S(ClientMessagePlayerUnload.TYPE, ClientMessagePlayerUnload.STREAM_CODEC, ClientMessagePlayerUnload::handle)
        registerC2S(ClientMessageSyncConfig.TYPE, ClientMessageSyncConfig.STREAM_CODEC, ClientMessageSyncConfig::handle)
        registerS2C(ServerMessageBroadcastSound.TYPE, ServerMessageBroadcastSound.STREAM_CODEC, ServerMessageBroadcastSound::handle)
        registerS2C(ServerMessageSyncConfig.TYPE, ServerMessageSyncConfig.STREAM_CODEC, ServerMessageSyncConfig::handle)
        registerLoginS2C(ServerMessageSyncConfig.TYPE, ServerMessageSyncConfig.STREAM_CODEC, ServerMessageSyncConfig::handleLogin)
    }

    fun <T : CustomPacketPayload> sendC2S(packet: T) {
        channel.sendToServer(packet)
    }

    fun <T : CustomPacketPayload> sendS2C(packet: T) {
        channel.send(PacketDistributor.ALL.noArg(), packet)
    }

    fun <T : CustomPacketPayload> sendS2C(player: ServerPlayer, packet: T) {
        channel.send(PacketDistributor.PLAYER.with { player }, packet)
    }

    inline fun <reified T : CustomPacketPayload> registerC2S(type: CustomPacketPayload.Type<T>, codec: StreamCodec<T>, handler: ServerHandler<T>) =
        registerC2S(T::class.java, type, codec, handler)

    fun <T : CustomPacketPayload> registerC2S(clazz: Class<T>, type: CustomPacketPayload.Type<T>, codec: StreamCodec<T>, handler: ServerHandler<T>) {
        channel.registerMessage(counter.getAndIncrement(), clazz, codec::encode, codec::decode, { packet, supplier ->
            val context = supplier.get()
            val player = context.sender
            handler.handle(packet, server ?: return@registerMessage, player)
            context.packetHandled = true
        }, Optional.of(NetworkDirection.PLAY_TO_SERVER))
    }

    inline fun <reified T : CustomPacketPayload> registerS2C(type: CustomPacketPayload.Type<T>, codec: StreamCodec<T>, handler: ClientHandler<T>) =
        registerS2C(T::class.java, type, codec, handler)

    fun <T : CustomPacketPayload> registerS2C(clazz: Class<T>, type: CustomPacketPayload.Type<T>, codec: StreamCodec<T>, handler: ClientHandler<T>) {
        channel.registerMessage(counter.getAndIncrement(), clazz, codec::encode, codec::decode, { packet, supplier ->
            val context = supplier.get()
            handler.handle(packet, Minecraft.getInstance())
            context.packetHandled = true
        }, Optional.of(NetworkDirection.PLAY_TO_CLIENT))
    }

    inline fun <reified T> registerLoginS2C(type: CustomPacketPayload.Type<T>, codec: StreamCodec<T>, handler: ClientHandler<T>) where T : LoginIndexedMessage, T : CustomPacketPayload =
        registerLoginS2C(T::class.java, type, codec, handler)

    fun <T> registerLoginS2C(clazz: Class<T>, type: CustomPacketPayload.Type<T>, codec: StreamCodec<T>, handler: ClientHandler<T>) where T : LoginIndexedMessage, T : CustomPacketPayload {
        handshake.messageBuilder(clazz, handshakeCounter.getAndIncrement(), NetworkDirection.LOGIN_TO_CLIENT)
            .loginIndex(LoginIndexedMessage::loginIndex, LoginIndexedMessage::loginIndex::set)
            .encoder(codec::encode)
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

    fun interface ClientHandler<T : CustomPacketPayload> {
        fun handle(packet: T, client: Minecraft)
    }

    fun interface ServerHandler<T : CustomPacketPayload> {
        fun handle(packet: T, server: MinecraftServer, player: ServerPlayer?)
    }
}