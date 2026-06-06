package me.muksc.tacztweaks

import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.Mixins
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class ModMixinPlugin : IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String) {
        Mixins.registerErrorHandlerClass("me.muksc.tacztweaks.ModMixinErrorHandler")
    }

    override fun getRefMapperConfig(): String? = null

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        for (manager in ModCompatibilityManager.ALL) {
            val prefix = manager.mixinPackagePrefix ?: continue
            if (!mixinClassName.startsWith(prefix)) continue
            return manager.loaded && manager.shouldApplyMixin(targetClassName, mixinClassName)
        }
        return true
    }

    override fun acceptTargets(myTargets: Set<String>, otherTargets: Set<String>) = Unit

    override fun getMixins(): List<String>? = null

    override fun preApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) = Unit

    override fun postApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) = Unit
}