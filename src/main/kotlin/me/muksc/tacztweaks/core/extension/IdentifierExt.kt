package me.muksc.tacztweaks.core.extension

import me.muksc.tacztweaks.core.registry.PlatformRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.Block

val Block.id: ResourceLocation? get() = PlatformRegistries.BLOCK.getKey(this)

val EntityType<*>.id: ResourceLocation? get() = PlatformRegistries.ENTITY_TYPE.getKey(this)