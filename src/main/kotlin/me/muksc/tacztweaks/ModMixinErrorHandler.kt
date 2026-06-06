package me.muksc.tacztweaks

import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager
import org.spongepowered.asm.mixin.extensibility.IMixinConfig
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

@Suppress("unused")
class ModMixinErrorHandler : IMixinErrorHandler {
    override fun onPrepareError(
        config: IMixinConfig,
        th: Throwable,
        mixin: IMixinInfo,
        action: IMixinErrorHandler.ErrorAction
    ): IMixinErrorHandler.ErrorAction = action

    override fun onApplyError(
        targetClassName: String,
        th: Throwable,
        mixin: IMixinInfo,
        action: IMixinErrorHandler.ErrorAction
    ): IMixinErrorHandler.ErrorAction {
        for (manager in ModCompatibilityManager.ALL) {
            val prefix = manager.mixinPackagePrefix ?: continue
            if (!mixin.className.startsWith(prefix)) continue
            manager.onError("Encountered an error while applying a '${manager.modId}' compatibility patch")
            return IMixinErrorHandler.ErrorAction.WARN
        }
        return action
    }
}