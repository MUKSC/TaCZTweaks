package me.muksc.tacztweaks.data.manager

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.anyOrEmpty
import me.muksc.tacztweaks.compat.lrtactical.LRTacticalCompat
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.core.BlockBreakingManager
import me.muksc.tacztweaks.core.Context
import me.muksc.tacztweaks.data.BulletInteraction
import me.muksc.tacztweaks.data.MeleeInteraction
import me.muksc.tacztweaks.data.manager.BulletInteractionManager.calcBlockBreakingDelta
import me.muksc.tacztweaks.thenPrioritizeBy
import net.minecraft.ChatFormatting
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.TierSortingRegistry
import net.minecraftforge.event.level.BlockEvent

private val COMPARATOR = compareBy<MeleeInteraction> { it.priority }
    .thenPrioritizeBy { it.target.isNotEmpty() }
    .thenPrioritizeBy { when (it) {
        is MeleeInteraction.Block -> it.blocks.isNotEmpty()
    } }

object MeleeInteractionManager : BaseDataManager<MeleeInteraction>("melee_interactions", COMPARATOR) {
    override fun notifyPlayer(player: ServerPlayer) {
        if (hasError()) {
            player.sendSystemMessage(TaCZTweaks.message()
                .append(TaCZTweaks.translatable("melee_interactions.error").withStyle(ChatFormatting.RED)))
        }
    }

    override fun debugEnabled(): Boolean = Config.Debug.meleeInteractions()

    override fun parseElement(json: JsonElement): MeleeInteraction =
        MeleeInteraction.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false) { /* Nothing */ }

    private inline fun <reified T : MeleeInteraction, E> getMeleeInteraction(
        weaponId: ResourceLocation,
        damage: Float,
        selector: (T) -> List<E>,
        predicate: (E) -> Boolean
    ): Pair<ResourceLocation, T>? = byType<T>().entries.firstOrNull { (_, interaction) ->
        interaction.target.anyOrEmpty { it.test(null, weaponId, damage) }
                && selector(interaction).anyOrEmpty(predicate)
    }?.toPair()

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
        logDebug { "Using block melee interaction: $id" }

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