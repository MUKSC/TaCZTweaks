@file:JvmName("DeferredHolderExt")
package me.muksc.tacztweaks.core.extension

import me.muksc.tacztweaks.core.registry.DeferredHolder
//? if >=1.21
//import net.minecraft.core.Holder

//? if <1.21 {
fun <R, T : R> DeferredHolder<R, T>.valueOrDelegate(): T = value()
//?} else {
/*fun <R, T : R> DeferredHolder<R, T>.valueOrDelegate(): Holder<R> = getDelegate()
*///?}