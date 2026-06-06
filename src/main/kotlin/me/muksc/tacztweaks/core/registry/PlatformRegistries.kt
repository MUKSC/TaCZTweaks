package me.muksc.tacztweaks.core.registry

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.level.block.Block

//? if forge {
import com.mojang.serialization.Codec
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.IForgeRegistry
//?} else if neoforge || fabric {
/*import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
*///?}

object PlatformRegistries {
    //? if forge {
    val BLOCK: IForgeRegistry<Block> = ForgeRegistries.BLOCKS
    val ATTRIBUTE: IForgeRegistry<Attribute> = ForgeRegistries.ATTRIBUTES
    val ENTITY_TYPE: IForgeRegistry<EntityType<*>> = ForgeRegistries.ENTITY_TYPES
    val MOB_EFFECT: IForgeRegistry<MobEffect> = ForgeRegistries.MOB_EFFECTS

    fun <T> IForgeRegistry<T>.byNameCodec(): Codec<T> = codec
    //?} else if neoforge || fabric {
    /*val BLOCK: Registry<Block> = BuiltInRegistries.BLOCK
    val ATTRIBUTE: Registry<Attribute> = BuiltInRegistries.ATTRIBUTE
    val ENTITY_TYPE: Registry<EntityType<*>> = BuiltInRegistries.ENTITY_TYPE
    val MOB_EFFECT: Registry<MobEffect> = BuiltInRegistries.MOB_EFFECT
    *///?}
}