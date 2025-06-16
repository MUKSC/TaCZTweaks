package me.muksc.tacztweaks.network

import net.minecraft.network.FriendlyByteBuf

fun interface StreamDecoder<T> {
    fun decode(buf: FriendlyByteBuf): T
}