package me.muksc.tacztweaks.feature.attribute

import com.tacz.guns.api.GunProperties
import com.tacz.guns.resource.modifier.AttachmentCacheProperty
import me.muksc.tacztweaks.core.extension.valueOrDelegate
import me.muksc.tacztweaks.registry.ModAttributes
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

object AttributeManager {
    fun onPropertyUpdated(entity: LivingEntity, stack: ItemStack, cache: AttachmentCacheProperty?) {
        val damage = cache?.getCache(GunProperties.DAMAGE)?.get(0)?.damage?.toDouble() ?: 0.0
        entity.getAttribute(ModAttributes.DAMAGE.valueOrDelegate())?.baseValue = damage
        for (attribute in arrayOf(
            ModAttributes.SHOOT_WHILE_SPRINTING,
            ModAttributes.SPRINT_WHILE_RELOADING
        )) {
            entity.getAttribute(attribute.valueOrDelegate())?.baseValue = attribute.value().defaultValue
        }
    }
}