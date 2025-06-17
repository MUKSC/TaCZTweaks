package me.muksc.tacztweaks.network.message

import java.util.function.IntSupplier

open class LoginIndexedMessage : IntSupplier {
    var loginIndex = 0

    override fun getAsInt(): Int = loginIndex
}