@file:Suppress("FunctionName")
package me.muksc.tacztweaks.core

import net.minecraft.resources.ResourceLocation

fun Identifier(namespace: String, path: String): ResourceLocation = /*? if <1.21 {*/ ResourceLocation(namespace, path) /*?} else {*/ /*ResourceLocation.fromNamespaceAndPath(namespace, path) *//*?}*/

fun Identifier(path: String): ResourceLocation = /*? if <1.21 {*/ ResourceLocation(path) /*?} else {*/ /*ResourceLocation.withDefaultNamespace(path) *//*?}*/