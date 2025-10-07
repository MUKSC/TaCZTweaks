package me.muksc.tacztweaks.compat.pillagers_gun

import com.scarasol.pillagers_gun.entity.projectile.Ammo
import net.minecraft.world.entity.Entity
import net.minecraftforge.fml.ModList

object PillagersGunCompat {
    private var enabled = false

    fun initialize() {
        if (ModList.get().isLoaded("pillagers_gun")) enabled = true
    }

    fun shouldIgnore(target: Entity, owner: Entity): Boolean {
        if (!enabled) return false
        return Ammo.checkFriendlyFire(target, owner)
    }
}