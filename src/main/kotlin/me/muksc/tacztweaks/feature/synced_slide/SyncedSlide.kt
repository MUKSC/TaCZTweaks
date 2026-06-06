@file:JvmName("SyncedSlide")
package me.muksc.tacztweaks.feature.synced_slide

import com.tacz.guns.api.TimelessAPI
import com.tacz.guns.api.item.IGun
import net.minecraft.client.player.LocalPlayer
import kotlin.jvm.optionals.getOrNull

val LocalPlayer.shouldSlide: Boolean
    get() {
        if (!IGun.mainHandHoldGun(this)) return false
        val display =  TimelessAPI.getGunDisplay(mainHandItem).getOrNull() ?: return false
        return display.animationStateMachine?.context?.shouldSlide() ?: false
    }