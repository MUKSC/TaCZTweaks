package me.muksc.tacztweaks.feature.general.compatibility

import me.muksc.tacztweaks.core.Identifier
import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager
import me.muksc.tacztweaks.core.extension.id
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB

object MTSManager : ModCompatibilityManager(
    modId = "mts",
    mixinPackagePrefix = "me.muksc.tacztweaks.mixin.feature.general.compatibility.mts."
) {
    @JvmStatic
    fun getBoundingBox(entity: Entity, original: () -> AABB): AABB {
        val id = entity.type.id ?: return original()
        return if (id == Inner.entityId) entity.boundingBox.inflate(0.3) else original()
    }

    private object Inner {
        val entityId = Identifier("mts", "builder_existing")
    }
}