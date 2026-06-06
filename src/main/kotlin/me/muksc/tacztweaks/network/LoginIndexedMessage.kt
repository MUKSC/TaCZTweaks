package me.muksc.tacztweaks.network

import me.muksc.tacztweaks.core.network.CustomPacketPayload
import java.util.function.IntSupplier

abstract class LoginIndexedMessage<T : CustomPacketPayload<T>> : CustomPacketPayload<T>, IntSupplier {
    var loginIndex = 0

    override fun getAsInt(): Int = loginIndex
}