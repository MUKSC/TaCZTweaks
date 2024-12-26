package me.muksc.tacztweaks

import com.tacz.guns.api.item.IAmmo
import com.tacz.guns.api.item.IAttachment
import com.tacz.guns.api.item.IGun
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

fun ItemStack.getTaCZId(): ResourceLocation? =
    IAmmo.getIAmmoOrNull(this)?.getAmmoId(this)
        ?: IAttachment.getIAttachmentOrNull(this)?.getAttachmentId(this)
        ?: IGun.getIGunOrNull(this)?.getGunId(this)