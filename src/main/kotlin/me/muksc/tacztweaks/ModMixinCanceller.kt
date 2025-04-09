package me.muksc.tacztweaks

import com.bawnorton.mixinsquared.api.MixinCanceller

class ModMixinCanceller : MixinCanceller {
    override fun shouldCancel(targetClassNames: List<String>, mixinClassName: String): Boolean = when {
        mixinClassName.startsWith("forge.io.github.xiewuzhiying.vs_addition.forge.mixin.tacz.") -> true
        else -> false
    }
}