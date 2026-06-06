package me.muksc.tacztweaks.core.network

//? if >=1.20.5 {
/*import me.muksc.tacztweaks.core.codec.StreamCodec
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.protocol.common.custom.CustomPacketPayload as MCCustomPacketPayload

interface CustomPacketPayload<T : CustomPacketPayload<T>> : MCCustomPacketPayload {
    fun self(): T

    override fun type(): CustomPacketPayloadType<T>

    fun codec(): StreamCodec<in RegistryFriendlyByteBuf, T>
}

typealias CustomPacketPayloadType<T> = MCCustomPacketPayload.Type<T>
*///?} else if forge {
import me.muksc.tacztweaks.core.codec.StreamCodec
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation

interface CustomPacketPayload<T : CustomPacketPayload<T>> {
    fun self(): T

    fun type(): CustomPacketPayloadType<T>

    fun codec(): StreamCodec<in FriendlyByteBuf, T>
}

data class CustomPacketPayloadType<T : CustomPacketPayload<T>>(val id: ResourceLocation)
//?} else if fabric {
/*import me.muksc.tacztweaks.core.codec.StreamCodec
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation

interface CustomPacketPayload<T : CustomPacketPayload<T>> : FabricPacket {
    fun self(): T

    fun type(): CustomPacketPayloadType<T>

    fun codec(): StreamCodec<in FriendlyByteBuf, T>

    override fun getType(): PacketType<T> = type().toPacketType(codec())

    override fun write(buf: FriendlyByteBuf) = codec().encode(buf, self())
}

data class CustomPacketPayloadType<T : CustomPacketPayload<T>>(val id: ResourceLocation) {
    fun toPacketType(codec: StreamCodec<in FriendlyByteBuf, T>): PacketType<T> = PacketType.create(id, codec::decode)
}
*///?}