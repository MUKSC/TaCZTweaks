package me.muksc.tacztweaks.core.resource

//? if forge || neoforge {
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener

interface IdentifiableResourceReloadListener : PreparableReloadListener {
    fun id(): ResourceLocation
}
//?} else if fabric {
/*import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener as FabricIdentifiableResourceReloadListener

interface IdentifiableResourceReloadListener : FabricIdentifiableResourceReloadListener, PreparableReloadListener {
    override fun getFabricId(): ResourceLocation = id()

    fun id(): ResourceLocation
}
*///?}