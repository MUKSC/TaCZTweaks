package me.muksc.tacztweaks.feature.disarm

import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.core.attribute.getBooleanAttributeValue
import me.muksc.tacztweaks.core.extension.valueOrDelegate
import me.muksc.tacztweaks.feature.gameplay.behaviour.isRowing
import me.muksc.tacztweaks.feature.general.compatibility.CuffedManager
import me.muksc.tacztweaks.registry.ModAttributes
import me.muksc.tacztweaks.registry.ModStatusEffects
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object DisarmManager {
    @JvmStatic
    fun getStatus(entity: LivingEntity): DisarmStatus {
        if (Config.General.Compatibility.cuffedCompat()) {
            if (entity is Player && CuffedManager.isItemUseDisabled(entity)) return DisarmStatus(true, false)
        }
        if (Config.Gameplay.Handling.disableWhileRowing()) {
            if (entity.isRowing()) return DisarmStatus(true, false)
        }
        if (entity.hasEffect(ModStatusEffects.DISARM.valueOrDelegate())) return DisarmStatus(true, true)
        if (!entity.getBooleanAttributeValue(ModAttributes.ENABLED.valueOrDelegate())) return DisarmStatus(true, true)
        return DisarmStatus(false, true)
    }

    @JvmStatic
    fun getStatus(): DisarmStatus {
        val player = Minecraft.getInstance().player ?: return DisarmStatus(false, true)
        return getStatus(player)
    }

    @JvmStatic
    fun shouldDisarm(entity: LivingEntity): Boolean = getStatus(entity).disarmed

    @JvmStatic
    fun shouldDisarm(): Boolean = getStatus().disarmed

    data class DisarmStatus(
        @JvmField val disarmed: Boolean,
        @JvmField val render: Boolean
    )
}