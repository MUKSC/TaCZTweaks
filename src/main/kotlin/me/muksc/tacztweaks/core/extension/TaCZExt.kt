@file:JvmName("TaCZExt")

package me.muksc.tacztweaks.core.extension

import com.tacz.guns.api.entity.IGunOperator
import com.tacz.guns.api.item.IAmmoBox
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder
import com.tacz.guns.client.resource.pojo.display.SoundData
import me.muksc.tacztweaks.mixin.accessor.LocalPlayerDataHolderAccessor
import me.muksc.tacztweaks.mixin.accessor.LocalPlayerShootAccessor
import me.muksc.tacztweaks.mixin.accessor.SoundDataAccessor
import me.muksc.tacztweaks.mixininterop.player
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

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
            || lockedCondition == LocalPlayerShootAccessor.`tacztweaks$SHOOT_LOCKED_CONDITION`()
        if (clientStateLock && !loosened) return true

        val operator = IGunOperator.fromLivingEntity((this as LocalPlayerDataHolderAccessor).player)
        return operator.synReloadState.stateType.isReloading
            || operator.synDrawCoolDown > 0
            || operator.synIsBolting
            || operator.synMeleeCoolDown > 0
    }

fun FriendlyByteBuf.writeSoundData(sound: SoundData) {
    writeResourceLocation(sound.sound)
    writeOptional(Optional.ofNullable(sound.volume), FriendlyByteBuf::writeFloat)
    writeOptional(Optional.ofNullable(sound.pitch), FriendlyByteBuf::writeFloat)
    writeFloat(sound.volumeRandomRange)
    writeFloat(sound.pitchRandomRange)
}

fun FriendlyByteBuf.readSoundData(): SoundData {
    val sound = readResourceLocation()
    val volume = readOptional(FriendlyByteBuf::readFloat).getOrNull()
    val pitch = readOptional(FriendlyByteBuf::readFloat).getOrNull()
    val volumeRandomRange = readFloat()
    val pitchRandomRange = readFloat()
    return SoundDataAccessor.`tacztweaks$newSoundData`(sound, volume, pitch, volumeRandomRange, pitchRandomRange)
}