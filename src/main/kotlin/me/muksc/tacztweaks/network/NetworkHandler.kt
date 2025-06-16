package me.muksc.tacztweaks.network

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.network.message.ClientMessagePlayerUnload
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
import java.util.concurrent.atomic.AtomicInteger

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = TaCZTweaks.MOD_ID)
object NetworkHandler {
    private var server: MinecraftServer? = null
    private val channel = NetworkRegistry.ChannelBuilder.named(TaCZTweaks.id("channel"))
        .networkProtocolVersion { "0" }
        .clientAcceptedVersions { true }
        .serverAcceptedVersions { true }
        .simpleChannel()
    private val counter = AtomicInteger()

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
        registerC2S(ClientMessagePlayerUnload.TYPE, ClientMessagePlayerUnload.STREAM_CODEC, ClientMessagePlayerUnload::handle)
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
        channel.registerMessage(counter.getAndIncrement(), clazz, codec::encode, codec::decode) { packet, supplier ->
            val context = supplier.get()
            if (context.direction != NetworkDirection.PLAY_TO_SERVER) return@registerMessage
            val player = context.sender
            handler.handle(packet, server ?: return@registerMessage, player)
            context.packetHandled = true
        }
    }

    inline fun <reified T : CustomPacketPayload> registerS2C(type: CustomPacketPayload.Type<T>, codec: StreamCodec<T>, handler: ClientHandler<T>) =
        registerS2C(T::class.java, type, codec, handler)

    fun <T : CustomPacketPayload> registerS2C(clazz: Class<T>, type: CustomPacketPayload.Type<T>, codec: StreamCodec<T>, handler: ClientHandler<T>) {
        channel.registerMessage(counter.getAndIncrement(), clazz, codec::encode, codec::decode) { packet, supplier ->
            val context = supplier.get()
            if (context.direction != NetworkDirection.PLAY_TO_SERVER) return@registerMessage
            handler.handle(packet, Minecraft.getInstance())
            context.packetHandled = true
        }
    }

    fun interface ClientHandler<T : CustomPacketPayload> {
        fun handle(packet: T, client: Minecraft)
    }

    fun interface ServerHandler<T : CustomPacketPayload> {
        fun handle(packet: T, server: MinecraftServer, player: ServerPlayer?)
    }
}