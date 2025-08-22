package me.muksc.tacztweaks.data

import com.google.common.collect.ImmutableMap
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.logging.LogUtils
import com.mojang.serialization.JsonOps
import me.muksc.tacztweaks.*
import me.muksc.tacztweaks.compat.lrtactical.LRTacticalCompat
import me.muksc.tacztweaks.data.BulletInteractionManager.calcBlockBreakingDelta
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.TierSortingRegistry
import net.minecraftforge.event.level.BlockEvent
import kotlin.reflect.KClass

private val GSON = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

object MeleeInteractionManager : SimpleJsonResourceReloadListener(GSON, "melee_interactions") {
    private val LOGGER = LogUtils.getLogger()
    private var error = false
    private var meleeInteractions: Map<KClass<*>, Map<ResourceLocation, MeleeInteraction>> = emptyMap()

    fun hasError(): Boolean = error

    private fun debug(msg: () -> String) {
        if (Config.Debug.meleeInteractions()) LOGGER.info(msg.invoke())
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : MeleeInteraction> byType(): Map<ResourceLocation, T> =
        meleeInteractions.getOrElse(T::class) { emptyMap() } as Map<ResourceLocation, T>

    private inline fun <reified T : MeleeInteraction, E> getMeleeInteraction(
        weaponId: ResourceLocation,
        damage: Float,
        selector: (T) -> List<E>,
        predicate: (E) -> Boolean
    ): Pair<ResourceLocation, T>? = byType<T>().entries.firstOrNull { (_, interaction) ->
        (interaction.target.isEmpty() || interaction.target.any { it.test(null, weaponId, damage) })
                && (selector(interaction).isEmpty() || selector(interaction).any(predicate))
    }?.toPair()

    override fun apply(
        map: Map<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profilerFilter: ProfilerFiller,
    ) {
        error = false
        val meleeInteractions = mutableMapOf<KClass<*>, ImmutableMap.Builder<ResourceLocation, MeleeInteraction>>()
        for ((resourceLocation, element) in map) {
            try {
                val interaction = MeleeInteraction.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow(false) { /* Nothing */ }
                meleeInteractions.computeIfAbsent(interaction::class) { ImmutableMap.builder() }.put(resourceLocation, interaction)
            } catch (e: RuntimeException) {
                LOGGER.error("Parsing error loading melee interaction $resourceLocation", e)
                error = true
            }
        }
        this.meleeInteractions = meleeInteractions.mapValues { entry -> entry.value.orderEntriesByValue(
            compareBy<MeleeInteraction> { it.priority }
                .thenPrioritizeBy { it.target.isNotEmpty() }
                .thenPrioritizeBy { when (it) {
                    is MeleeInteraction.Block -> it.blocks.isNotEmpty()
                } }
        ).build() }.toImmutableMap()
    }

    fun handleBlockInteraction(player: ServerPlayer, reach: Double, damage: Float) {
        val level = player.level() as? ServerLevel ?: return
        val stack = player.mainHandItem
        val weaponId = Context.Gun(stack).id ?: LRTacticalCompat.getWeaponId(stack) ?: return
        val result = player.pick(reach, 1.0F, false)
        if (result !is BlockHitResult || result.type == HitResult.Type.MISS) return

        val blockPos = result.blockPos
        val state = level.getBlockState(blockPos)
        val (id, interaction) = getMeleeInteraction(weaponId, damage, MeleeInteraction.Block::blocks) {
            it.test(level, result.blockPos, state)
        } ?: return
        debug { "Using block melee interaction: $id" }

        val breakBlock = run {
            val hardness = state.getDestroySpeed(level, blockPos)
            if (hardness !in interaction.blockBreak.hardness) return@run false
            val isCorrectToolForDrops = when (interaction.blockBreak.tier) {
                null -> true
                else -> TierSortingRegistry.isCorrectTierForDrops(interaction.blockBreak.tier, state)
            }
            if (!isCorrectToolForDrops) return@run false

            when (interaction.blockBreak) {
                is BulletInteraction.Block.BlockBreak.Never -> false
                is BulletInteraction.Block.BlockBreak.Instant -> true
                is BulletInteraction.Block.BlockBreak.Count -> {
                    val delta = BlockBreakingManager.addCurrentProgress(level, blockPos, 1.0F / interaction.blockBreak.count)
                    delta >= 1.0F
                }
                is BulletInteraction.Block.BlockBreak.FixedDamage -> {
                    val damage = interaction.blockBreak.damage
                    var delta = calcBlockBreakingDelta(damage, 0.0, state, level, blockPos)
                    if (interaction.blockBreak.accumulate) delta = BlockBreakingManager.addCurrentProgress(level, blockPos, delta)
                    delta >= 1.0F
                }
                is BulletInteraction.Block.BlockBreak.DynamicDamage -> {
                    var delta = calcBlockBreakingDelta(damage, 0.0, state, level, blockPos)
                    if (interaction.blockBreak.accumulate) delta = BlockBreakingManager.addCurrentProgress(level, blockPos, delta)
                    delta >= 1.0F
                }
            }
        }
        if (breakBlock) run {
            val cancelled = MinecraftForge.EVENT_BUS.post(BlockEvent.BreakEvent(level, blockPos, state, player))
            if (cancelled) return@run
            level.destroyBlock(blockPos, interaction.blockBreak.drop, player)
        }
    }
}