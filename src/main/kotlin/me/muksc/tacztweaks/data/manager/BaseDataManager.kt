package me.muksc.tacztweaks.data.manager

import com.google.common.collect.ImmutableMap
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.logging.LogUtils
import me.muksc.tacztweaks.toImmutableMap
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraftforge.server.ServerLifecycleHooks
import org.slf4j.Logger
import kotlin.reflect.KClass

private val GSON = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

abstract class BaseDataManager<E : Any>(
    private val directory: String,
    private val elementComparator: Comparator<E>
) : SimpleJsonResourceReloadListener(GSON, directory) {
    protected val logger: Logger = LogUtils.getLogger()
    protected var map: Map<KClass<*>, Map<ResourceLocation, E>> = emptyMap()
    private var hasError = false

    abstract fun notifyPlayer(player: ServerPlayer)

    abstract fun debugEnabled(): Boolean

    abstract fun parseElement(json: JsonElement): E

    fun logDebug(msg: () -> String) {
        if (debugEnabled()) logger.info(msg.invoke())
    }

    fun hasError(): Boolean = hasError

    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified T: E> byType(): Map<ResourceLocation, T> =
        map.getOrElse(T::class) { emptyMap() } as Map<ResourceLocation, T>

    @Suppress("UnstableApiUsage")
    override fun apply(
        elements: Map<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller
    ) {
        hasError = false
        val data = mutableMapOf<KClass<*>, ImmutableMap.Builder<ResourceLocation, E>>()
        for ((id, json) in elements) {
            try {
                val element = parseElement(json)
                data.computeIfAbsent(element::class) { ImmutableMap.builder() }.put(id, element)
            } catch (e: RuntimeException) {
                logger.error("Parsing error loading $directory", e)
                hasError = true
            }
        }
        map = data.mapValues { it.value.orderEntriesByValue(elementComparator).build() }.toImmutableMap()

        run {
            val server = ServerLifecycleHooks.getCurrentServer() ?: return@run
            for (player in server.playerList.players) {
                notifyPlayer(player)
            }
        }
    }
}