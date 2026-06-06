package me.muksc.tacztweaks.core.notify

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager
import me.muksc.tacztweaks.core.getModInfo
import net.minecraft.ChatFormatting
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

object PlayerNotifier {
    private val notifiedPlayers = Object2ObjectOpenHashMap<UUID, ObjectOpenHashSet<String>>()

    fun onPlayerJoin(player: ServerPlayer) {
        checkAndNotify(player)
    }

    fun onServerTick(server: MinecraftServer) {
        if (server.tickCount % 20 != 0) return
        for (player in server.playerList.players) {
            checkAndNotify(player)
        }
    }

    fun onPlayerLeave(player: ServerPlayer) {
        notifiedPlayers.remove(player.uuid)
    }

    private fun checkAndNotify(player: ServerPlayer) {
        val set = notifiedPlayers.computeIfAbsent(player.uuid) { ObjectOpenHashSet() }
        val managers = ModCompatibilityManager.ALL.filter { it.hasError && it.modId !in set }
        if (managers.isEmpty()) return

        val mods = managers.joinToString("\n") {
            val mod = getModInfo(it.modId) ?: return@joinToString "- ${it.modId}"
            "- ${mod.name} ${mod.version}"
        }
        player.sendSystemMessage(TaCZTweaks.message().append(
            TaCZTweaks.translatable("compatibility_manager.error", mods)
                .withStyle(ChatFormatting.RED)
        ))
        set.addAll(managers.map { it.modId })
    }
}