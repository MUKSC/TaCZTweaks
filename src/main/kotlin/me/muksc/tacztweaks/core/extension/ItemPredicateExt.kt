package me.muksc.tacztweaks.core.extension

//? if <1.20.5 {
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.world.item.ItemStack

fun ItemPredicate.test(stack: ItemStack): Boolean = matches(stack)
//?}