package me.muksc.tacztweaks.mixininterop

import me.muksc.tacztweaks.mixin.accessor.LocalPlayerDataHolderAccessor
import net.minecraft.client.player.LocalPlayer

inline val LocalPlayerDataHolderAccessor.player: LocalPlayer
    get() = `tacztweaks$getPlayer`()