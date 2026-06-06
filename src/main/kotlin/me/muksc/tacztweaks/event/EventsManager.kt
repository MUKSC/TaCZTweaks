package me.muksc.tacztweaks.event

import me.muksc.tacztweaks.client.input.BoltKey
import me.muksc.tacztweaks.client.input.TiltGunKey
import me.muksc.tacztweaks.client.input.UnloadKey
import me.muksc.tacztweaks.config.ConfigManager
import me.muksc.tacztweaks.core.notify.PlayerNotifier
import me.muksc.tacztweaks.feature.datapack.legacy.manager.BaseDataManager
import me.muksc.tacztweaks.feature.datapack.legacy.manager.BulletParticlesManager
import me.muksc.tacztweaks.feature.datapack.shield.CustomShieldEvents
import me.muksc.tacztweaks.feature.destroy_progress.DestroyProgressManager
import me.muksc.tacztweaks.platform.PlatformEvents
import me.muksc.tacztweaks.platform.PlatformEventsClient
import net.minecraft.server.level.ServerLevel

object EventsManager {
    fun initialize(platform: PlatformEvents) {
        platform.onPlayerJoin { player ->
            PlayerNotifier.onPlayerJoin(player)
        }
        platform.onServerTick { server ->
            PlayerNotifier.onServerTick(server)
        }
        platform.onPlayerLeave { player ->
            PlayerNotifier.onPlayerLeave(player)
        }
        platform.onLevelTick { level ->
            if (level is ServerLevel) {
                DestroyProgressManager.onLevelTick(level)
                BulletParticlesManager.onLevelTick(level)
            }
        }
        platform.onBlockBreak { level, pos ->
            if (level is ServerLevel) DestroyProgressManager.onBlockBreak(level, pos)
        }
        platform.onShieldBlock(CustomShieldEvents::onDamageBlock)
        platform.onDataPackSync { player ->
            for (manager in BaseDataManager.ALL) {
                manager.notifyPlayer(player)
            }
        }
    }

    fun initializeClient(platform: PlatformEventsClient) {
        platform.onClientLoggingOut(ConfigManager::onLoggingOut)
        platform.onEndClientTick {
            BoltKey.onEndClientTick()
            TiltGunKey.onEndClientTick()
            UnloadKey.onEndClientTick()
        }
    }
}