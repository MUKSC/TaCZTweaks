@file:JvmName("TaCZExt")

package me.muksc.tacztweaks.core.extension

import com.tacz.guns.api.entity.IGunOperator
import com.tacz.guns.api.item.IAmmoBox
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder
import me.muksc.tacztweaks.mixin.accessor.LocalPlayerDataHolderAccessor
import me.muksc.tacztweaks.mixin.accessor.LocalPlayerShootAccessor
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack

fun Inventory.hasCreativeAmmoBox(gunStack: ItemStack): Boolean = (0..containerSize).any { index ->
    val stack = getItem(index)
    val item = stack.item
    if (item !is IAmmoBox) return@any false
    if (!item.isAmmoBoxOfGun(gunStack, stack)) return@any false
    item.isAllTypeCreative(stack) || item.isCreative(stack)
}

val LocalPlayerDataHolder.clientStateLockExcludingShoot: Boolean
    get() {
        val loosened = lockedCondition == null
            || lockedCondition == LocalPlayerShootAccessor.SHOOT_LOCKED_CONDITION()
        if (clientStateLock && !loosened) return true

        val operator = IGunOperator.fromLivingEntity((this as LocalPlayerDataHolderAccessor).player)
        return operator.synReloadState.stateType.isReloading
            || operator.synDrawCoolDown > 0
            || operator.synIsBolting
            || operator.synMeleeCoolDown > 0
    }