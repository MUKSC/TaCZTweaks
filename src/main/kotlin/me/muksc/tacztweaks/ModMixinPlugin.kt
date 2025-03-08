package me.muksc.tacztweaks

import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.loading.LoadingModList
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class ModMixinPlugin : IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String) { /* Nothing */ }

    override fun getRefMapperConfig(): String? = null

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean = when {
        mixinClassName.startsWith("me.muksc.tacztweaks.mixin.compat.firstaid.") -> isModLoaded("firstaid")
        mixinClassName.startsWith("me.muksc.tacztweaks.mixin.compat.lso.") -> isModLoaded("legendarysurvivaloverhaul")
        mixinClassName.startsWith("me.muksc.tacztweaks.mixin.compat.vs.") -> isModLoaded("valkyrienskies")
        else -> true
    }

    override fun acceptTargets(myTargets: Set<String>, otherTargets: Set<String>) { /* Nothing */ }

    override fun getMixins(): List<String>? = null

    override fun preApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo,
    ) { /* Nothing */ }

    override fun postApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo,
    ) { /* Nothing */ }

    private fun isModLoaded(id: String): Boolean =
        ModList.get()?.isLoaded(id) == true || LoadingModList.get()?.getModFileById(id) != null
}