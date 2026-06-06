package me.muksc.tacztweaks.feature.general.compatibility

import com.tacz.guns.entity.EntityKineticBullet
import com.tacz.guns.util.TacHitResult
import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager

//? if 1.20.1 && forge {
import ichttt.mods.firstaid.common.EventHandler
import net.minecraftforge.event.entity.ProjectileImpactEvent
//?}

object FirstAidManager : ModCompatibilityManager(
    modId = "firstaid",
    mixinPackagePrefix = "me.muksc.tacztweaks.mixin.feature.general.compatibility.firstaid."
) {
    @JvmStatic
    fun onHitEntity(bullet: EntityKineticBullet, result: TacHitResult) = withFallback(Unit) {
        Inner.onProjectileImpact(bullet, result)
    }

    private object Inner {
        fun onProjectileImpact(bullet: EntityKineticBullet, result: TacHitResult) {
            //? if 1.20.1 && forge
            EventHandler.onProjectileImpact(ProjectileImpactEvent(bullet, result))
        }
    }
}