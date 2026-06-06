package me.muksc.tacztweaks.feature.general.compatibility

import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager
import me.muksc.tacztweaks.core.modVersionRange
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

//? if forge || neoforge
import me.xjqsh.lrtactical.api.item.IMeleeWeapon

object LRTacticalManager : ModCompatibilityManager(
    modId = "lrtactical",
    mixinPackagePrefix = "me.muksc.tacztweaks.mixin.feature.general.compatibility.lrtactical"
) {
    fun getWeaponId(stack: ItemStack): ResourceLocation? = withFallback(null) {
        Inner.getWeaponId(stack)
    }

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean = when (mixinClassName) {
        "me.muksc.tacztweaks.mixin.feature.general.compatibility.lrtactical.MeleeItemMixin_0_4_0" -> modVersionRange("lrtactical", "0.4.0")
        "me.muksc.tacztweaks.mixin.feature.general.compatibility.lrtactical.MeleeItemMixin_0_3_0" -> modVersionRange("lrtactical", "0.3.0", "0.4.0")
        else -> true
    }

    private object Inner {
        fun getWeaponId(stack: ItemStack): ResourceLocation? =
            /*? if forge || neoforge {*/ IMeleeWeapon.of(stack)?.getId(stack) /*?} else {*/ /*null *//*?}*/
    }
}