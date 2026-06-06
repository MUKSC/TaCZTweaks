package me.muksc.tacztweaks.feature.general.compatibility

import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager
import net.minecraft.world.entity.Entity

//? if 1.20.1 && forge
import com.scarasol.pillagers_gun.entity.projectile.Ammo

object PillagersGunManager : ModCompatibilityManager(
    modId = "pillagers_gun",
    mixinPackagePrefix = null
) {
    fun shouldIgnoreEntity(target: Entity, owner: Entity?): Boolean = withFallback(false) {
        Inner.checkFriendlyFire(target, owner ?: return@withFallback false)
    }

    private object Inner {
        fun checkFriendlyFire(target: Entity, owner: Entity): Boolean =
            /*? if 1.20.1 && forge {*/ Ammo.checkFriendlyFire(target, owner) /*?} else {*/ /*false *//*?}*/
    }
}