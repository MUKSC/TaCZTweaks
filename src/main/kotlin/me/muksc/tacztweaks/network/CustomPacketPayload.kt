package me.muksc.tacztweaks.network

import net.minecraft.resources.ResourceLocation

interface CustomPacketPayload {
    fun type(): Type<*>

    data class Type<T : CustomPacketPayload>(val id: ResourceLocation)
}