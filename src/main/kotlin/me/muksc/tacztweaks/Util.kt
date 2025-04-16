package me.muksc.tacztweaks

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.tacz.guns.api.item.IAmmo
import com.tacz.guns.api.item.IAttachment
import com.tacz.guns.api.item.IGun
import me.muksc.tacztweaks.data.StrictOptionalFieldCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

fun ItemStack.getTaCZId(): ResourceLocation? =
    IAmmo.getIAmmoOrNull(this)?.getAmmoId(this)
        ?: IAttachment.getIAttachmentOrNull(this)?.getAttachmentId(this)
        ?: IGun.getIGunOrNull(this)?.getGunId(this)

fun <T : Any> Codec<T>.strictOptionalFieldOf(name: String): MapCodec<Optional<T>> =
    StrictOptionalFieldCodec(name, this)

fun <T : Any> Codec<T>.strictOptionalFieldOf(name: String, defaultValue: T): MapCodec<T> =
    StrictOptionalFieldCodec(name, this).xmap(
        { it.orElse(defaultValue) },
        { if (it == defaultValue) Optional.empty() else Optional.of(it) }
    )

fun <T> singleOrListCodec(codec: Codec<T>): Codec<List<T>> =
    Codec.either(codec, Codec.list(codec))
        .xmap({ it.left().map { listOf(it) }.getOrNull() ?: it.right().get() }) { when {
            it.size == 1 -> Either.left(it.first())
            else -> Either.right(it)
        } }
