package me.muksc.tacztweaks

import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.loading.LoadingModList
import org.apache.maven.artifact.versioning.VersionRange
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class ModMixinPlugin : IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String) { /* Nothing */ }

    override fun getRefMapperConfig(): String? = null

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean = when {
        mixinClassName.startsWith("me.muksc.tacztweaks.mixin.compat.firstaid.") -> isModLoaded("firstaid")
        mixinClassName.startsWith("me.muksc.tacztweaks.mixin.compat.lrtactical.") -> isModLoaded("lrtactical")
        mixinClassName.startsWith("me.muksc.tacztweaks.mixin.compat.lso.") -> isModLoaded("legendarysurvivaloverhaul")
        mixinClassName.startsWith("me.muksc.tacztweaks.mixin.compat.mts.") -> isModLoaded("mts")
        mixinClassName.startsWith("me.muksc.tacztweaks.mixin.compat.soundphysics.") -> isModLoaded("sound_physics_remastered") && when (mixinClassName) {
            "me.muksc.tacztweaks.mixin.compat.soundphysics.SoundPhysicsMixin$1_5_x" -> versionCheck("sound_physics_remastered", "[1.20.1-1.5.0,)")
            "me.muksc.tacztweaks.mixin.compat.soundphysics.SoundPhysicsMixin$1_1_x" -> versionCheck("sound_physics_remastered", "(,1.20.1-1.5.0)")
            else -> true
        }
        mixinClassName.startsWith("me.muksc.tacztweaks.mixin.compat.vs.") -> isModLoaded("valkyrienskies") && when (mixinClassName) {
            "me.muksc.tacztweaks.mixin.compat.vs.client.MixinLevelRendererMixin" -> !isModLoaded("vs_addition")
            else -> true
        }
        mixinClassName.startsWith("me.muksc.tacztweaks.mixin.compat.vs_addition.") -> isModLoaded("vs_addition")
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

    private fun versionCheck(id: String, spec: String): Boolean {
        val info = ModList.get()?.getModFileById(id) ?: LoadingModList.get()?.getModFileById(id) ?: return false
        println("version: ${info.mods[0].version}, spec: $spec, artifact: ${VersionRange.createFromVersionSpec(spec)}, result: ${VersionRange.createFromVersionSpec(spec).containsVersion(info.mods[0].version)}")
        return VersionRange.createFromVersionSpec(spec).containsVersion(info.mods[0].version)
    }
}