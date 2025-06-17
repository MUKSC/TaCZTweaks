package me.muksc.tacztweaks.config

import me.muksc.tacztweaks.Config
import me.muksc.tacztweaks.TaCZTweaks
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = TaCZTweaks.MOD_ID, value = [Dist.CLIENT])
object ConfigManager {
    var syncedWithServer = false

    fun canUpdateServerConfig(): Boolean {
        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return false
        return canUpdateServerConfig(player)
    }

    fun canUpdateServerConfig(player: Player) =
        player.hasPermissions(2)

    @JvmStatic
    @SubscribeEvent
    fun onLoggingOut(e: ClientPlayerNetworkEvent.LoggingOut) {
        syncedWithServer = false
        Config.sync(ESyncDirection.RESET)
    }
}