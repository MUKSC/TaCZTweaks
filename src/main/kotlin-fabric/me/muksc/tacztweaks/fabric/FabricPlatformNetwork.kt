package me.muksc.tacztweaks.fabric

import me.muksc.tacztweaks.core.codec.StreamCodec
import me.muksc.tacztweaks.core.network.CustomPacketPayload
import me.muksc.tacztweaks.core.network.CustomPacketPayloadType
import me.muksc.tacztweaks.network.LoginIndexedMessage
import me.muksc.tacztweaks.platform.PlatformNetwork
import me.muksc.tacztweaks.platform.PlatformNetwork.ClientHandler
import me.muksc.tacztweaks.platform.PlatformNetwork.ServerHandler
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import java.util.concurrent.CompletableFuture

//? if >=1.20.5 {
/*import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.network.RegistryFriendlyByteBuf
*///?} else {
import net.minecraft.network.FriendlyByteBuf as RegistryFriendlyByteBuf
//?}

object FabricPlatformNetwork : PlatformNetwork {
    override fun <T : CustomPacketPayload<T>> sendC2S(packet: T) {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            ClientPlayNetworking.send(packet)
        }
    }

    override fun <T : CustomPacketPayload<T>> sendS2C(server: MinecraftServer, packet: T) {
        for (player in server.playerList.players) {
            ServerPlayNetworking.send(player, packet)
        }
    }

    override fun <T : CustomPacketPayload<T>> sendS2C(
        player: ServerPlayer,
        packet: T
    ) {
        ServerPlayNetworking.send(player, packet)
    }

    override fun <T : CustomPacketPayload<T>> registerC2S(
        clazz: Class<T>,
        type: CustomPacketPayloadType<T>,
        codec: StreamCodec<in RegistryFriendlyByteBuf, T>,
        handler: ServerHandler<T>
    ) {
        //? if <1.20.5 {
        ServerPlayNetworking.registerGlobalReceiver(type.toPacketType(codec)) { packet, player, sender ->
            handler.handle(packet, player.server, player)
        }
        //?} else {
        /*PayloadTypeRegistry.playC2S().register(type, codec)
        ServerPlayNetworking.registerGlobalReceiver(type) { packet, context ->
            handler.handle(packet, context.server(), context.player())
        }
        *///?}
    }

    override fun <T : CustomPacketPayload<T>> registerS2C(
        clazz: Class<T>,
        type: CustomPacketPayloadType<T>,
        codec: StreamCodec<in RegistryFriendlyByteBuf, T>,
        handler: ClientHandler<T>
    ) {
        //? if <1.20.5 {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(type.toPacketType(codec)) { packet, player, sender ->
                handler.handle(packet, Minecraft.getInstance())
            }
        }
        //?} else {
        /*PayloadTypeRegistry.playS2C().register(type, codec)
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(type) { packet, context ->
                handler.handle(packet, Minecraft.getInstance())
            }
        }
        *///?}
    }

    override fun <T : LoginIndexedMessage<T>> registerLoginS2C(
        clazz: Class<T>,
        type: CustomPacketPayloadType<T>,
        codec: StreamCodec<in FriendlyByteBuf, T>,
        handler: ClientHandler<T>
    ) {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            ClientLoginNetworking.registerGlobalReceiver(type.id) { minecraft, listener, buf, consumer ->
                handler.handle(codec.decode(buf), minecraft)
                CompletableFuture.completedFuture(PacketByteBufs.empty())
            }
        }
        ServerLoginConnectionEvents.QUERY_START.register { listener, server, sender, synchronizer ->
            sender.sendPacket(type.id, PacketByteBufs.create().apply {
                codec.encode(this, clazz.getConstructor().newInstance())
            })
        }
        ServerLoginNetworking.registerGlobalReceiver(type.id) { server, listener, understood, buf, synchronizer, sender ->
            if (!understood) listener.disconnect(Component.translatable("disconnect.disconnected"))
        }
    }
}