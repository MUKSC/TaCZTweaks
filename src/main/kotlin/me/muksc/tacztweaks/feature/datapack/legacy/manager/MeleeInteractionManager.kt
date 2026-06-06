package me.muksc.tacztweaks.feature.datapack.legacy.manager

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.core.anyOrEmpty
import me.muksc.tacztweaks.core.tacz.GunStack
import me.muksc.tacztweaks.feature.datapack.legacy.MeleeInteraction
import me.muksc.tacztweaks.feature.general.compatibility.LRTacticalManager
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

//? if <1.20.5
import me.muksc.tacztweaks.core.extension.getOrThrow

private val COMPARATOR = compareBy<MeleeInteraction> { it.priority }
    .thenByDescending { it.target.isNotEmpty() }
    .thenByDescending { when (it) {
        is MeleeInteraction.Block -> it.blocks.isNotEmpty()
    } }

object MeleeInteractionManager : BaseDataManager<MeleeInteraction>("melee_interactions", COMPARATOR) {
    val ID = TaCZTweaks.id("melee_interactions")
    override fun id(): ResourceLocation = ID

    override val debugEnabled: Boolean get() = Config.General.Debug.meleeInteractions()

    override fun parseElement(json: JsonElement): MeleeInteraction =
        MeleeInteraction.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()

    private inline fun <reified T : MeleeInteraction, E> getMeleeInteraction(
        weaponId: ResourceLocation,
        damage: Float,
        selector: (T) -> List<E>,
        predicate: (E) -> Boolean
    ): Pair<ResourceLocation, T>? = byType<T>().entries.firstOrNull { (_, interaction) ->
        interaction.target.anyOrEmpty { it.test(null, weaponId, damage) }
            && selector(interaction).anyOrEmpty(predicate)
    }?.toPair()

    @JvmStatic
    fun handleBlockInteraction(entity: LivingEntity, reach: Double, damage: Float) {
        val level = entity.level() as? ServerLevel ?: return
        val stack = entity.mainHandItem
        val result = entity.pick(reach, 1.0F, false)
        if (result !is BlockHitResult || result.type == HitResult.Type.MISS) return

        val blockPos = result.blockPos
        val state = level.getBlockState(blockPos)
        val weaponId = GunStack(stack).id ?: LRTacticalManager.getWeaponId(stack) ?: return
        val (id, interaction) = getMeleeInteraction(weaponId, damage, MeleeInteraction.Block::blocks) {
            it.test(level, result.blockPos, state)
        } ?: return
        logger.infoDebug("Using melee interaction: $id")

        val destroyBlock = BulletInteractionManager.shouldDestroyBlock(
            interaction.blockBreak, 0.0,
            state, level, blockPos
        ) { damage }
        if (destroyBlock) BulletInteractionManager.destroyBlock(interaction.blockBreak, entity, state, level, blockPos)
    }
}