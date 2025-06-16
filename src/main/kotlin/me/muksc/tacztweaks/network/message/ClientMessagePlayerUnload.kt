package me.muksc.tacztweaks.network.message

import com.tacz.guns.api.item.IGun
import me.muksc.tacztweaks.AbstractGunItemExtension
import me.muksc.tacztweaks.Config
import me.muksc.tacztweaks.ShooterDataHolderProvider
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.network.CustomPacketPayload
import me.muksc.tacztweaks.network.StreamCodec
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.common.util.LogicalSidedProvider

object ClientMessagePlayerUnload : CustomPacketPayload {
    val TYPE = CustomPacketPayload.Type<ClientMessagePlayerUnload>(
        TaCZTweaks.id("client_player_unload")
    )
    val STREAM_CODEC = StreamCodec.of(
        encoder = { packet, buf -> /* Nothing */ },
        decoder = { buf -> ClientMessagePlayerUnload }
    )

    fun handle(packet: ClientMessagePlayerUnload, server: MinecraftServer, player: ServerPlayer?) {
        if (!Config.Gun.allowUnload()) return
        server.execute {
            if (player == null) return@execute
            val data = (player as ShooterDataHolderProvider).`tacztweaks$getShooterDataHolder`()
            val gunStack = data.currentGunItem?.get() ?: return@execute
            val gun = IGun.getIGunOrNull(gunStack) ?: return@execute
            val ext = gun as? AbstractGunItemExtension ?: return@execute
            ext.`tacztweaks$setUnloading`()
            LogicalSidedProvider.WORKQUEUE
            gun.dropAllAmmo(player, gunStack)
        }
    }

    override fun type(): CustomPacketPayload.Type<ClientMessagePlayerUnload> = TYPE
}