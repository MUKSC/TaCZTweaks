package me.muksc.tacztweaks.network.message

import com.tacz.guns.api.entity.IGunOperator
import com.tacz.guns.api.item.IGun
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.core.network.CustomPacketPayload
import me.muksc.tacztweaks.core.network.CustomPacketPayloadType
import me.muksc.tacztweaks.core.codec.StreamCodec
import me.muksc.tacztweaks.core.extension.hasCreativeAmmoBox
import me.muksc.tacztweaks.mixininterface.feature.keyactions.unload.UnloadableGun
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object ClientMessagePlayerUnload : CustomPacketPayload<ClientMessagePlayerUnload> {
    val TYPE = CustomPacketPayloadType<ClientMessagePlayerUnload>(
        TaCZTweaks.id("client_player_unload")
    )
    val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ClientMessagePlayerUnload> =
        StreamCodec.unit(ClientMessagePlayerUnload)

    fun handle(packet: ClientMessagePlayerUnload, server: MinecraftServer, player: ServerPlayer?) = server.execute {
        if (!Config.KeyActions.Unload.enabled() || player == null) return@execute
        val operator = IGunOperator.fromLivingEntity(player)
        val gunStack = operator.dataHolder.currentGunItem?.get() ?: return@execute
        if (player.inventory.hasCreativeAmmoBox(gunStack)) return@execute
        val gun = IGun.getIGunOrNull(gunStack) ?: return@execute
        UnloadableGun.of(gun).`tacztweaks$unload`(player, gunStack)
    }

    override fun self(): ClientMessagePlayerUnload = this

    override fun type(): CustomPacketPayloadType<ClientMessagePlayerUnload> = TYPE

    override fun codec(): StreamCodec<FriendlyByteBuf, ClientMessagePlayerUnload> = STREAM_CODEC
}