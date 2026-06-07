@file:Suppress("NOTHING_TO_INLINE")

package me.muksc.tacztweaks.mixininterop

import me.muksc.tacztweaks.mixininterface.feature.keyactions.unload.UnloadableGun
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

inline fun UnloadableGun.unload(player: Player, gunItem: ItemStack) =
    `tacztweaks$unload`(player, gunItem)