package me.muksc.tacztweaks.config.migration

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.logger.withMarker
import me.muksc.tacztweaks.mixin.accessor.JsonFileCodecConfigAccessor
import org.slf4j.MarkerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import kotlin.io.path.copyTo
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension

val logger = TaCZTweaks.logger.withMarker(
    MarkerFactory.getMarker("ConfigMigration")
)

fun JsonFileCodecConfig<*>.migrate(currentVersion: Int) {
    val accessor = this as JsonFileCodecConfigAccessor
    if (Files.notExists(accessor.configPath)) return
    val json = try {
        Files.readString(accessor.configPath)
    } catch (e: IOException) {
        logger.error("Failed to read config file", e)
        return
    }
    val jsonTree = try {
        JsonParser.parseString(json)
    } catch (e: JsonParseException) {
        logger.error("Failed to decode config file", e)
        return
    }

    if (jsonTree !is JsonObject) {
        logger.error("Malformed config file - not an object")
        return
    }
    val versionElement: JsonElement? = jsonTree["version"]
    if (versionElement != null && (versionElement !is JsonPrimitive || !versionElement.isNumber)) {
        logger.error("Malformed config file - version is not a number")
        return
    }

    val version = versionElement?.asInt ?: 0
    val backup = accessor.configPath.resolveSibling(
        accessor.configPath.run { "$nameWithoutExtension-backup-v$version.$extension" }
    )
    accessor.configPath.copyTo(backup, overwrite = true)

    var migrated = jsonTree
    for (migrateFromVersion in version..<currentVersion) {
        val migrateToVersion = migrateFromVersion + 1
        migrated = when (migrateFromVersion + 1) {
            1 -> migrateToV1(jsonTree)
            else -> throw NotImplementedError("Migration to v$migrateToVersion is not implemented")
        }
    }
    if (migrated === jsonTree) return

    try {
        Files.writeString(
            accessor.configPath,
            accessor.gson.toJson(migrated),
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.CREATE
        )
    } catch (e: IOException) {
        logger.error("Failed to write config file", e)
        return
    }
    logger.info("Migration complete")
}