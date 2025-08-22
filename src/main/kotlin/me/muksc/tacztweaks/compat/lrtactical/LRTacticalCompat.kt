package me.muksc.tacztweaks.compat.lrtactical

import me.xjqsh.lrtactical.api.item.IMeleeWeapon
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fml.ModList

object LRTacticalCompat {
    private var enabled = false

    fun isEnabled(): Boolean = enabled

    fun initialize() {
        if (ModList.get().isLoaded("lrtactical")) enabled = true
    }

    fun getWeaponId(stack: ItemStack): ResourceLocation? {
        if (!enabled) return null
        return IMeleeWeapon.of(stack)?.getId(stack)
    }
}