package me.muksc.tacztweaks.client

import com.tacz.guns.api.item.IAmmo
import com.tacz.guns.api.item.IAttachment
import com.tacz.guns.api.item.IGun
import com.tacz.guns.client.resource.ClientAssetManager
import com.tacz.guns.client.resource.pojo.PackInfo
import com.tacz.guns.crafting.GunSmithTableRecipe
import me.muksc.tacztweaks.getTaCZId
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation

class Recipes(
    val recipes: Map<ResourceLocation, GunSmithTableRecipe>,
    val packs: Map<String, PackInfo>,
    val idToPackId: Map<ResourceLocation, String>
) {
    companion object {
        fun getRecipes(original: Map<ResourceLocation, GunSmithTableRecipe>): Recipes {
            val recipes: Map<ResourceLocation, GunSmithTableRecipe> = run {
                val player = Minecraft.getInstance().player ?: return@run null
                val gunStack = player.mainHandItem ?: return@run null
                val gun = IGun.getIGunOrNull(gunStack) ?: return@run null
                original.filter { (_, recipe) ->
                    IAmmo.getIAmmoOrNull(recipe.output)?.let { return@filter it.isAmmoOfGun(gunStack, recipe.output) }
                    IAttachment.getIAttachmentOrNull(recipe.output)?.let { return@filter gun.allowAttachment(gunStack, recipe.output) }
                    true
                }
            } ?: original
            val idToPack = original.values.mapNotNull { recipe ->
                val id = recipe.output.getTaCZId() ?: return@mapNotNull null
                val packInfo = ClientAssetManager.INSTANCE.getPackInfo(id) ?: return@mapNotNull null
                id to packInfo
            }.toMap()
            val packs = idToPack.map { (id, pack) -> id.namespace to pack }.toMap()
            return Recipes(recipes, packs, idToPack.mapValues { it.key.namespace })
        }
    }
}