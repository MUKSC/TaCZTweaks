package me.muksc.tacztweaks.feature.datapack.legacy.manager

import com.google.common.collect.ImmutableMap
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.logger.withMarker
import me.muksc.tacztweaks.core.resource.IdentifiableResourceReloadListener
import me.muksc.tacztweaks.core.toImmutableMap
import net.minecraft.ChatFormatting
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import org.slf4j.Logger
import org.slf4j.MarkerFactory
import kotlin.reflect.KClass

private val GSON = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

abstract class BaseDataManager<E : Any>(
    private val directory: String,
    private val elementComparator: Comparator<E>
) : SimpleJsonResourceReloadListener(GSON, directory), IdentifiableResourceReloadListener {
    companion object {
        val ALL by lazy { listOf(
            BulletInteractionManager,
            BulletParticlesManager,
            BulletSoundsManager,
            MeleeInteractionManager
        ) }
    }

    val logger = TaCZTweaks.logger.withMarker(
        MarkerFactory.getMarker(this::class.simpleName)
    )
    protected var map: Map<KClass<*>, Map<ResourceLocation, E>> = emptyMap()

    protected var hasError: Boolean = false
        private set

    protected abstract val debugEnabled: Boolean

    abstract fun parseElement(json: JsonElement): E

    open fun notifyPlayer(player: ServerPlayer) {
        if (hasError) {
            player.sendSystemMessage(TaCZTweaks.message().append(
                TaCZTweaks.translatable("datapack.data_manager.error", directory)
                    .withStyle(ChatFormatting.RED)
            ))
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified T: E> byType(): Map<ResourceLocation, T> =
        map.getOrElse(T::class) { emptyMap() } as Map<ResourceLocation, T>

    override fun apply(
        elements: Map<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller
    ) {
        hasError = false
        map = buildMap<KClass<*>, ImmutableMap.Builder<ResourceLocation, E>> {
            for ((id, json) in elements) {
                try {
                    val element = parseElement(json)
                    computeIfAbsent(element::class) {
                        ImmutableMap.builder()
                    }.put(id, element)
                } catch (e: RuntimeException) {
                    logger.error("Parsing error loading $directory, id $id", e)
                    hasError = true
                }
            }
        }.mapValues { (_, value) ->
            value.orderEntriesByValue(elementComparator).build()
        }.toImmutableMap()
    }

    fun Logger.infoDebug(msg: String) {
        if (debugEnabled) info(msg)
    }
}