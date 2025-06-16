package me.muksc.tacztweaks.network

import net.minecraft.network.FriendlyByteBuf

fun interface StreamEncoder<T> {
    fun encode(packet: T, buf: FriendlyByteBuf)
}