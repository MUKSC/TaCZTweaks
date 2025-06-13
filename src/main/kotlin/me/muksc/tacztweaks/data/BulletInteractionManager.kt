package me.muksc.tacztweaks.data

import com.google.common.collect.ImmutableMap
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.authlib.GameProfile
import com.mojang.logging.LogUtils
import com.mojang.serialization.JsonOps
import com.tacz.guns.entity.EntityKineticBullet
import com.tacz.guns.util.AttachmentDataUtils
import com.tacz.guns.util.TacHitResult
import me.muksc.tacztweaks.BlockBreakingManager
import me.muksc.tacztweaks.Context
import me.muksc.tacztweaks.DestroySpeedModifierHolder
import me.muksc.tacztweaks.EntityKineticBulletExtension
import me.muksc.tacztweaks.data.old.convert
import me.muksc.tacztweaks.mixin.accessor.EntityKineticBulletAccessor
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerPlayerGameMode
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.item.Tier
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.TierSortingRegistry
import net.minecraftforge.common.util.FakePlayer
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.math.exp
import kotlin.reflect.KClass
import me.muksc.tacztweaks.data.old.BulletInteraction as OldBulletInteraction

private val GSON = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

object BulletInteractionManager : SimpleJsonResourceReloadListener(GSON, "bullet_interactions") {
    private val LOGGER = LogUtils.getLogger()
    private val FAKE_PROFILE = GameProfile(UUID.fromString("BF8411E4-9730-4215-9AE8-1688EEDF9B72"), "[Minecraft]")
    private var error = false
    private var bulletInteractions: Map<KClass<*>, Map<ResourceLocation, BulletInteraction>> = emptyMap()

    fun hasError(): Boolean = error

    init { BulletInteraction }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : BulletInteraction> byType(): Map<ResourceLocation, T> =
        bulletInteractions.getOrElse(T::class) { emptyMap() } as Map<ResourceLocation, T>

    private inline fun <reified T : BulletInteraction, E> getBulletInteraction(
        entity: EntityKineticBullet,
        location: Vec3,
        selector: (T) -> List<E>,
        predicate: (E) -> Boolean
    ): T? = byType<T>().values.run {
        filter { interaction -> interaction.target.test(entity, location) }.run {
            firstOrNull { interaction ->
                selector.invoke(interaction).any(predicate)
            } ?: firstOrNull { interaction ->
                selector.invoke(interaction).isEmpty()
            }
        } ?: filter { interaction -> interaction.target is Target.Fallback }.run {
            firstOrNull { interaction ->
                selector.invoke(interaction).any(predicate)
            } ?: firstOrNull { interaction ->
                selector.invoke(interaction).isEmpty()
            }
        }
    }

    override fun apply(
        map: Map<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profileFiller: ProfilerFiller,
    ) {
        error = false
        val bulletInteractions = mutableMapOf<KClass<*>, MutableMap<ResourceLocation, BulletInteraction>>()
        for ((resourceLocation, element) in map) {
            try {
                val interaction = try {
                    BulletInteraction.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow(false) { /* Nothing */ }
                } catch (e: RuntimeException) {
                    OldBulletInteraction.CODEC.parse(JsonOps.INSTANCE, element)
                        .resultOrPartial { /* Nothing */ }.getOrNull()
                        ?.convert() ?: throw e
                }
                bulletInteractions.computeIfAbsent(interaction::class) { mutableMapOf() }[resourceLocation] = interaction
            } catch (e: RuntimeException) {
                LOGGER.error("Parsing error loading bullet interaction $resourceLocation", e)
                error = true
            }
        }
        this.bulletInteractions = ImmutableMap.copyOf(bulletInteractions)
    }

    fun handleBlockInteraction(ammo: EntityKineticBullet, result: BlockHitResult, state: BlockState): InteractionResult {
        val ext = ammo as EntityKineticBulletExtension
        val interaction = getBulletInteraction(ammo, result.location, BulletInteraction.Block::blocks) {
            it.test(state)
        } ?: BulletInteraction.Block.DEFAULT

        val level = ammo.level() as ServerLevel
        val blockPos = BlockPos(result.blockPos)
        val breakBlock = run {
            val hardness = state.getDestroySpeed(level, blockPos)
            if (hardness !in interaction.blockBreak.hardness) return@run false
            val isCorrectToolForDrops = when (interaction.blockBreak.tier) {
                null -> true
                else -> TierSortingRegistry.isCorrectTierForDrops(interaction.blockBreak.tier, state)
            }
            if (!isCorrectToolForDrops) return@run false

            val gun = Context.Gun(ext.`tacztweaks$getGunStack`())
            val armorIgnore = AttachmentDataUtils.getArmorIgnoreWithAttachment(gun.stack, gun.index?.gunData)
            when (interaction.blockBreak) {
                is BulletInteraction.Block.BlockBreak.Never -> false
                is BulletInteraction.Block.BlockBreak.Instant -> true
                is BulletInteraction.Block.BlockBreak.Count -> {
                    val delta = BlockBreakingManager.addCurrentProgress(level, blockPos, 1.0F / interaction.blockBreak.count)
                    delta >= 1.0F
                }
                is BulletInteraction.Block.BlockBreak.FixedDamage -> {
                    val damage = interaction.blockBreak.damage
                    var delta = calcBlockBreakingDelta(damage, armorIgnore, state, level, blockPos)
                    if (interaction.blockBreak.accumulate) delta = BlockBreakingManager.addCurrentProgress(level, blockPos, delta)
                    delta >= 1.0F
                }
                is BulletInteraction.Block.BlockBreak.DynamicDamage -> {
                    val damage = interaction.blockBreak.run { (ammo.getDamage(result.location) + modifier) * multiplier }
                    var delta = calcBlockBreakingDelta(damage, armorIgnore, state, level, blockPos)
                    if (interaction.blockBreak.accumulate) delta = BlockBreakingManager.addCurrentProgress(level, blockPos, delta)
                    delta >= 1.0F
                }
            }
        }
        if (breakBlock) run {
            val owner = ammo.owner
            if (owner is ServerPlayer) {
                val exp = ForgeHooks.onBlockBreakEvent(level, owner.gameMode.gameModeForPlayer, owner, result.blockPos)
                if (exp == -1) return@run
            }
            level.destroyBlock(result.blockPos, interaction.blockBreak.drop, ammo.owner)
        }
        return InteractionResult(shouldPierce(ammo, result, interaction, breakBlock, ext::`tacztweaks$incrementBlockPierce`, ext::`tacztweaks$getBlockPierce`), breakBlock)
    }

    fun handleEntityInteraction(ammo: EntityKineticBullet, result: TacHitResult, context: ClipContext): InteractionResult {
        val ext = ammo as EntityKineticBulletExtension
        val accessor = ammo as EntityKineticBulletAccessor
        val entity = result.entity
        val interaction = getBulletInteraction(ammo, result.location, BulletInteraction.Entity::entities) {
            it.test(entity)
        } ?: BulletInteraction.Entity.DEFAULT

        ext.`tacztweaks$addDamageModifier`(interaction.damage.modifier, interaction.damage.multiplier)
        try {
            accessor.invokeOnHitEntity(result, context.from, result.location)
        } catch (_: Exception) {
            ext.`tacztweaks$popDamageModifier`()
        }
        val isDead = !entity.isAlive
        if (accessor.explosion) return InteractionResult(false, isDead)
        return InteractionResult(shouldPierce(ammo, result, interaction, isDead, ext::`tacztweaks$incrementEntityPierce`, ext::`tacztweaks$getEntityPierce`), isDead)
    }

    private fun shouldPierce(
        ammo: EntityKineticBullet, result: HitResult, interaction: BulletInteraction, condition: Boolean,
        incrementCustomPierce: () -> Unit, getCustomPierce: () -> Int
    ): Boolean {
        val accessor = ammo as EntityKineticBulletAccessor
        val ext = ammo as EntityKineticBulletExtension
        if (interaction.gunPierce.consume) accessor.pierce -= 1
        if (interaction.gunPierce.required && accessor.pierce <= 0) return false
        when (interaction.pierce) {
            is BulletInteraction.Pierce.Never -> false
            is BulletInteraction.Pierce.Default -> true
            is BulletInteraction.Pierce.Count -> {
                incrementCustomPierce.invoke()
                getCustomPierce.invoke() < interaction.pierce.count
            }
            is BulletInteraction.Pierce.Damage -> ammo.getDamage(result.location) > 0.0F
        } || return false
        if (interaction.pierce.conditional && !condition) return false
        ext.`tacztweaks$addDamageModifier`(
            -interaction.pierce.damageFalloff,
            interaction.pierce.damageMultiplier
        )
        return true
    }

    private fun calcBlockBreakingDelta(damage: Float, armorIgnore: Double, state: BlockState, level: ServerLevel, pos: BlockPos): Float {
        val player = object : FakePlayer(level, FAKE_PROFILE) {
            override fun getDigSpeed(state: BlockState, pos: BlockPos?): Float =
                (super.getDigSpeed(state, pos) + damage).toFloat()

            override fun hasCorrectToolForDrops(pState: BlockState): Boolean = true
        }
        val holder = (state.block as DestroySpeedModifierHolder)
        return try {
            holder.`tacztweaks$setDestroySpeedMultiplier`(remapArmorIgnore(armorIgnore))
            state.getDestroyProgress(player, level, pos)
        } finally {
            holder.`tacztweaks$setDestroySpeedMultiplier`(1.0F)
        }
    }

    private fun remapArmorIgnore(armorIgnore: Double): Float =
        exp(-2 * armorIgnore).toFloat()

    data class InteractionResult(
        val pierce: Boolean,
        val condition: Boolean
    )
}