package me.muksc.tacztweaks

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.tacz.guns.api.item.IAmmo
import com.tacz.guns.api.item.IAttachment
import com.tacz.guns.api.item.IGun
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrNull

fun ItemStack.getTaCZId(): ResourceLocation? =
    IAmmo.getIAmmoOrNull(this)?.getAmmoId(this)
        ?: IAttachment.getIAttachmentOrNull(this)?.getAttachmentId(this)
        ?: IGun.getIGunOrNull(this)?.getGunId(this)

fun <T> singleOrListCodec(codec: Codec<T>): Codec<List<T>> =
    Codec.either(codec, Codec.list(codec))
        .xmap({ it.left().map { listOf(it) }.getOrNull() ?: it.right().get() }) { when {
            it.size == 1 -> Either.left(it.first())
            else -> Either.right(it)
        } }
