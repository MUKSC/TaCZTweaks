package me.muksc.tacztweaks.core

//? if forge {
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.loading.FMLPaths
import net.minecraftforge.fml.loading.LoadingModList
import net.minecraftforge.forgespi.language.IModFileInfo
import org.apache.maven.artifact.versioning.VersionRange
import java.nio.file.Path

private fun getModFile(id: String): IModFileInfo? =
    LoadingModList.get()?.getModFileById(id) ?: ModList.get()?.getModFileById(id)

private fun modVersionMatch(id: String, version: String): Boolean {
    val info = getModFile(id) ?: return false
    return VersionRange.createFromVersionSpec(version).containsVersion(info.mods[0].version)
}

fun getModInfo(id: String): ModInfo? {
    val info = getModFile(id) ?: return null
    return ModInfo(
        id = info.mods[0].modId,
        name = info.mods[0].displayName,
        version = info.mods[0].version.toString()
    )
}

fun isModLoaded(id: String): Boolean = getModFile(id) != null

fun modVersionRange(id: String, startInclusive: String): Boolean =
    modVersionMatch(id, "[${startInclusive},)")

fun modVersionRange(id: String, startInclusive: String, endExclusive: String): Boolean =
    modVersionMatch(id, "[${startInclusive},${endExclusive})")

val GAME_DIR: Path = FMLPaths.GAMEDIR.get()
//?} else if neoforge {
/*import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLPaths
import net.neoforged.fml.loading.LoadingModList
import net.neoforged.neoforgespi.language.IModFileInfo
import org.apache.maven.artifact.versioning.VersionRange
import java.nio.file.Path

private fun getModFile(id: String): IModFileInfo? =
    LoadingModList.get()?.getModFileById(id) ?: ModList.get()?.getModFileById(id)

private fun modVersionMatch(id: String, version: String): Boolean {
    val info = getModFile(id) ?: return false
    return VersionRange.createFromVersionSpec(version).containsVersion(info.mods[0].version)
}

fun getModInfo(id: String): ModInfo? {
    val info = getModFile(id) ?: return null
    return ModInfo(
        id = info.mods[0].modId,
        name = info.mods[0].displayName,
        version = info.mods[0].version.toString()
    )
}

fun isModLoaded(id: String): Boolean = getModFile(id) != null

fun modVersionRange(id: String, startInclusive: String): Boolean =
    modVersionMatch(id, "[${startInclusive},)")

fun modVersionRange(id: String, startInclusive: String, endExclusive: String): Boolean =
    modVersionMatch(id, "[${startInclusive},${endExclusive})")

val GAME_DIR: Path = FMLPaths.GAMEDIR.get()
*///?} else if fabric {
/*import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.metadata.version.VersionPredicate
import java.nio.file.Path
import kotlin.jvm.optionals.getOrNull

private fun getModContainer(id: String): ModContainer? =
    FabricLoader.getInstance().getModContainer(id).getOrNull()

private fun modVersionMatch(id: String, version: String): Boolean {
    val container = getModContainer(id) ?: return false
    return VersionPredicate.parse(version).test(container.metadata.version)
}

fun getModInfo(id: String): ModInfo? {
    val container = getModContainer(id) ?: return null
    return ModInfo(
        id = container.metadata.id,
        name = container.metadata.name,
        version = container.metadata.version.friendlyString
    )
}

fun isModLoaded(id: String): Boolean =
    FabricLoader.getInstance().isModLoaded(id)

fun modVersionRange(id: String, startInclusive: String): Boolean =
    modVersionMatch(id, ">=${startInclusive}")

fun modVersionRange(id: String, startInclusive: String, endExclusive: String): Boolean =
    modVersionMatch(id, ">=${startInclusive} <${endExclusive}")

val GAME_DIR: Path = FabricLoader.getInstance().gameDir
*///?}

data class ModInfo(
    val id: String,
    val name: String,
    val version: String
)