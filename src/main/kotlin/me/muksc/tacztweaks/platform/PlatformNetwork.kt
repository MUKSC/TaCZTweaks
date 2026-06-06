package me.muksc.tacztweaks.platform

import me.muksc.tacztweaks.core.network.CustomPacketPayload
import me.muksc.tacztweaks.core.network.CustomPacketPayloadType
import me.muksc.tacztweaks.core.codec.StreamCodec
import me.muksc.tacztweaks.network.LoginIndexedMessage
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

//? if >=1.20.5 {
/*import net.minecraft.network.RegistryFriendlyByteBuf
*///?} else {
import net.minecraft.network.FriendlyByteBuf as RegistryFriendlyByteBuf
//?}

interface PlatformNetwork {
    fun <T : CustomPacketPayload<T>> sendC2S(packet: T)

    fun <T : CustomPacketPayload<T>> sendS2C(server: MinecraftServer, packet: T)

    fun <T : CustomPacketPayload<T>> sendS2C(player: ServerPlayer, packet: T)

    fun <T : CustomPacketPayload<T>> registerC2S(
        clazz: Class<T>,
        type: CustomPacketPayloadType<T>,
        codec: StreamCodec<in RegistryFriendlyByteBuf, T>,
        handler: ServerHandler<T>
    )

    fun <T : CustomPacketPayload<T>> registerS2C(
        clazz: Class<T>,
        type: CustomPacketPayloadType<T>,
        codec: StreamCodec<in RegistryFriendlyByteBuf, T>,
        handler: ClientHandler<T>
    )

    fun <T : LoginIndexedMessage<T>> registerLoginS2C(
        clazz: Class<T>,
        type: CustomPacketPayloadType<T>,
        codec: StreamCodec<in FriendlyByteBuf, T>,
        handler: ClientHandler<T>
    )

    fun interface ClientHandler<T : CustomPacketPayload<T>> {
        fun handle(packet: T, client: Minecraft)
    }

    fun interface ServerHandler<T : CustomPacketPayload<T>> {
        fun handle(packet: T, server: MinecraftServer, player: ServerPlayer)
    }

    companion object {
        inline fun <reified T : CustomPacketPayload<T>> PlatformNetwork.registerC2S(
            type: CustomPacketPayloadType<T>,
            codec: StreamCodec<in RegistryFriendlyByteBuf, T>,
            handler: ServerHandler<T>
        ) =
            registerC2S(T::class.java, type, codec, handler)

        inline fun <reified T : CustomPacketPayload<T>> PlatformNetwork.registerS2C(
            type: CustomPacketPayloadType<T>,
            codec: StreamCodec<in RegistryFriendlyByteBuf, T>,
            handler: ClientHandler<T>
        ) =
            registerS2C(T::class.java, type, codec, handler)

        inline fun <reified T : LoginIndexedMessage<T>> PlatformNetwork.registerLoginS2C(
            type: CustomPacketPayloadType<T>,
            codec: StreamCodec<in FriendlyByteBuf, T>,
            handler: ClientHandler<T>
        ) =
            registerLoginS2C(T::class.java, type, codec, handler)
    }
}