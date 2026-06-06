package me.muksc.tacztweaks.core.extension

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

val JsonElement.asJsonObjectOrNull: JsonObject? get() = this as? JsonObject

val JsonElement.asJsonArrayOrNull: JsonArray? get() = this as? JsonArray

val JsonElement.asBooleanOrNull: Boolean? get() = if (this is JsonPrimitive && isBoolean) asBoolean else null

val JsonElement.asDoubleOrNull: Double? get() = if (this is JsonPrimitive && isNumber) asDouble else null

val JsonElement.asFloatOrNull: Float? get() = if (this is JsonPrimitive && isNumber) asFloat else null

fun JsonObject.getJsonObjectOrNull(memberName: String): JsonObject? = get(memberName)?.asJsonObjectOrNull

fun JsonObject.getJsonArrayOrNull(memberName: String): JsonArray? = get(memberName)?.asJsonArrayOrNull

fun JsonObject.getBooleanOrNull(memberName: String): Boolean? = get(memberName)?.asBooleanOrNull

fun JsonObject.getDoubleOrNull(memberName: String): Double? = get(memberName)?.asDoubleOrNull

fun JsonObject.getFloatOrNull(memberName: String): Float? = get(memberName)?.asFloatOrNull