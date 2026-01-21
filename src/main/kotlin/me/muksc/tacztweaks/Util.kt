package me.muksc.tacztweaks

import com.google.common.collect.ImmutableMap
import net.minecraft.commands.arguments.blocks.BlockInput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.registries.ForgeRegistries

fun <T> identity(value: T): T = value

inline fun <T> Collection<T>.anyOrEmpty(predicate: (T) -> Boolean): Boolean = isEmpty() || any(predicate)

fun <K, V> Map<K, V>.toImmutableMap(): ImmutableMap<K, V> =
    ImmutableMap.copyOf(this)

inline fun <T> Comparator<T>.thenPrioritizeBy(crossinline selector: (T) -> Boolean): Comparator<T> =
    thenByDescending(selector)

fun <T> Class<T>.setPrivateField(instance: T, name: String, value: Any?) {
    getDeclaredField(name).run {
        isAccessible = true
        set(instance, value)
    }
}

fun BlockState.blockInput(): BlockInput = BlockInput(this, emptySet(), null)

val Block.id: ResourceLocation?
    get() = ForgeRegistries.BLOCKS.getKey(this)

val EntityType<*>.id: ResourceLocation?
    get() = ForgeRegistries.ENTITY_TYPES.getKey(this)
