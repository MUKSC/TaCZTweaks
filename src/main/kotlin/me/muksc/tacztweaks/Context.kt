package me.muksc.tacztweaks

import com.tacz.guns.api.TimelessAPI
import com.tacz.guns.api.item.IGun
import com.tacz.guns.entity.EntityKineticBullet
import com.tacz.guns.resource.index.CommonGunIndex
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrNull

object Context {
    lateinit var ammo: EntityKineticBullet

    object Gun {
        var stack: ItemStack? = null
        val gun: IGun?
            get() = IGun.getIGunOrNull(stack)
        val id: ResourceLocation?
            get() = gun?.getGunId(stack)
        val index: CommonGunIndex?
            get() = TimelessAPI.getCommonGunIndex(id).getOrNull()
    }
}