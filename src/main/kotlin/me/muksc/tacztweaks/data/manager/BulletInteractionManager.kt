package me.muksc.tacztweaks.data.manager

import com.google.gson.JsonElement
import com.mojang.authlib.GameProfile
import com.mojang.serialization.JsonOps
import com.tacz.guns.entity.EntityKineticBullet
import com.tacz.guns.init.ModDamageTypes
import com.tacz.guns.particles.BulletHoleOption
import com.tacz.guns.util.AttachmentDataUtils
import com.tacz.guns.util.TacHitResult
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.core.BlockBreakingManager
import me.muksc.tacztweaks.core.Context
import me.muksc.tacztweaks.data.BulletInteraction
import me.muksc.tacztweaks.data.old.convert
import me.muksc.tacztweaks.mixin.accessor.EntityKineticBulletAccessor
import me.muksc.tacztweaks.mixininterface.features.EntityKineticBulletExtension
import me.muksc.tacztweaks.mixininterface.features.bullet_interaction.DestroySpeedModifierHolder
import me.muksc.tacztweaks.mixininterface.features.bullet_interaction.ShieldInteractionBehaviour
import me.muksc.tacztweaks.thenPrioritizeBy
import net.minecraft.ChatFormatting
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.TierSortingRegistry
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.event.entity.living.ShieldBlockEvent
import net.minecraftforge.event.level.BlockEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.math.exp
import me.muksc.tacztweaks.data.old.BulletInteraction as OldBulletInteraction

private val COMPARATOR = compareBy<BulletInteraction> { it.priority }
    .thenPrioritizeBy { it.target.isNotEmpty() }
    .thenPrioritizeBy { when (it) {
        is BulletInteraction.Block -> it.blocks.isNotEmpty()
        is BulletInteraction.Entity -> it.entities.isNotEmpty()
        is BulletInteraction.Shield -> it.predicate != ItemPredicate.ANY
    } }

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = TaCZTweaks.MOD_ID)
object BulletInteractionManager : BaseDataManager<BulletInteraction>("bullet_interactions", COMPARATOR) {
    private val FAKE_PROFILE = GameProfile(UUID.fromString("BF8411E4-9730-4215-9AE8-1688EEDF9B72"), "[Minecraft]")
    private val DEFAULT = TaCZTweaks.id("default")

    init { BulletInteraction }

    override fun notifyPlayer(player: ServerPlayer) {
        if (hasError()) {
            player.sendSystemMessage(TaCZTweaks.message()
                .append(TaCZTweaks.translatable("bullet_interactions.error").withStyle(ChatFormatting.RED)))
        }
    }

    override fun debugEnabled(): Boolean = Config.Debug.bulletInteractions()

    override fun parseElement(json: JsonElement): BulletInteraction =
        try {
            BulletInteraction.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false) { /* Nothing */ }
        } catch (e: RuntimeException) {
            OldBulletInteraction.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial { /* Nothing */ }.getOrNull()
                ?.convert() ?: throw e
        }

    private inline fun <reified T : BulletInteraction, E> getBulletInteraction(
        entity: EntityKineticBullet,
        location: Vec3,
        selector: (T) -> List<E>,
        predicate: (E) -> Boolean
    ): Pair<ResourceLocation, T>? = byType<T>().entries.firstOrNull { (_, interaction) ->
        (interaction.target.isEmpty() || interaction.target.any { it.test(entity, entity.gunId, entity.getDamage(location)) })
                && (selector(interaction).isEmpty() || selector(interaction).any(predicate))
    }?.toPair()

    private inline fun <reified T : BulletInteraction> getBulletInteraction(
        entity: EntityKineticBullet,
        location: Vec3,
        predicate: (T) -> Boolean
    ): Pair<ResourceLocation, T>? = byType<T>().entries.firstOrNull { (_, interaction) ->
        (interaction.target.isEmpty() || interaction.target.any { it.test(entity, entity.gunId, entity.getDamage(location)) })
                && predicate(interaction)
    }?.toPair()

    fun handleBlockInteraction(ammo: EntityKineticBullet, result: BlockHitResult, state: BlockState): InteractionResult {
        val level = ammo.level() as ServerLevel
        val blockPos = BlockPos(result.blockPos)
        val ext = ammo as EntityKineticBulletExtension
        val (id, interaction) = getBulletInteraction(ammo, result.location, BulletInteraction.Block::blocks) {
            it.test(level, blockPos, state)
        } ?: (DEFAULT to BulletInteraction.Block.DEFAULT)
        logDebug { "Using block bullet interaction: $id" }

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
                val cancelled = MinecraftForge.EVENT_BUS.post(BlockEvent.BreakEvent(level, blockPos, state, owner))
                if (cancelled) return@run
            }
            level.destroyBlock(blockPos, interaction.blockBreak.drop, ammo.owner)
            val replaceWith = interaction.blockBreak.replaceWith
            if (!replaceWith.state.isAir && replaceWith.place(level, blockPos, Block.UPDATE_CLIENTS)) {
                level.blockUpdated(blockPos, replaceWith.state.block)
            }
        }
        val pierce = shouldPierce(ammo, result, interaction.pierce, interaction.gunPierce, breakBlock, ext::`tacztweaks$incrementBlockPierce`, ext::`tacztweaks$getBlockPierce`)
        if (pierce && !breakBlock && interaction.pierce.renderBulletHole) {
            val bulletHoleOption = BulletHoleOption(
                result.direction,
                blockPos,
                ammo.ammoId.toString(),
                ammo.gunId.toString(),
                ammo.gunDisplayId.toString()
            )
            level.sendParticles(bulletHoleOption, result.location.x, result.location.y, result.location.z, 1, 0.0, 0.0, 0.0, 0.0)
        }
        return InteractionResult(pierce, breakBlock)
            .also { logDebug { it.toString() } }
    }

    fun handleEntityInteraction(ammo: EntityKineticBullet, result: TacHitResult, context: ClipContext): InteractionResult {
        val ext = ammo as EntityKineticBulletExtension
        val accessor = ammo as EntityKineticBulletAccessor
        val entity = result.entity
        val (id, interaction) = getBulletInteraction(ammo, result.location, BulletInteraction.Entity::entities) {
            it.test(entity)
        } ?: (DEFAULT to BulletInteraction.Entity.DEFAULT)
        logDebug { "Using entity bullet interaction: $id" }

        ext.`tacztweaks$addDamageModifier`(interaction.damage.modifier, interaction.damage.multiplier)
        try {
            accessor.invokeOnHitEntity(result, context.from, result.location)
        } catch (_: Exception) {
            ext.`tacztweaks$popDamageModifier`()
        }
        val isDead = !entity.isAlive
        if (accessor.explosion) return InteractionResult(false, isDead).also { logDebug { it.toString() } }
        return InteractionResult(shouldPierce(ammo, result, interaction.pierce, interaction.gunPierce, isDead, ext::`tacztweaks$incrementEntityPierce`, ext::`tacztweaks$getEntityPierce`), isDead)
            .also { logDebug { it.toString() } }
    }

    fun handleShieldInteraction(ammo: EntityKineticBullet, location: Vec3, shield: ItemStack, originalDamage: Float): ShieldInteractionResult {
        val (id, interaction) = getBulletInteraction<BulletInteraction.Shield>(ammo, location) {
            it.predicate.matches(shield)
        } ?: (DEFAULT to BulletInteraction.Shield.DEFAULT)
        logDebug { "Using shield bullet interaction: $id" }

        val damage = (originalDamage - interaction.damage.falloff) * interaction.damage.multiplier
        val durabilityDamage = lambda@ { durabilityDamage: Int ->
            if (interaction.durability.conditional && damage <= 0) return@lambda 0
            when (interaction.durability) {
                is BulletInteraction.Shield.Durability.DynamicDamage -> ((durabilityDamage + interaction.durability.modifier) * interaction.durability.multiplier).toInt()
                is BulletInteraction.Shield.Durability.FixedDamage -> interaction.durability.damage
            }
        }
        val disableDuration = run {
            if (interaction.disable.conditional && damage <= 0) return@run 0
            if (ammo.random.nextFloat() < interaction.disable.chance) {
                interaction.disable.duration
            } else {
                0
            }
        }
        return ShieldInteractionResult(originalDamage - damage, durabilityDamage, disableDuration)
    }

    @JvmStatic
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onShieldBlock(e: ShieldBlockEvent) {
        if (!e.damageSource.`is`(ModDamageTypes.BULLETS_TAG)) return
        val ammo = e.damageSource.directEntity as? EntityKineticBullet ?: return
        val location = e.damageSource.sourcePosition ?: return
        val behaviour = e.entity as ShieldInteractionBehaviour
        val result = handleShieldInteraction(ammo, location, e.entity.useItem, e.originalBlockedDamage)

        if (result.blockedDamage <= 0) {
            e.isCanceled = true
            return
        }
        e.blockedDamage = result.blockedDamage
        e.setShieldTakesDamage(true)
        behaviour.`tacztweaks$setCustomShieldDurabilityDamage`(result.durabilityDamage)
        behaviour.`tacztweaks$setCustomShieldDisableDuration`(result.disableDuration)
    }

    private fun shouldPierce(
        ammo: EntityKineticBullet, result: HitResult,
        pierce: BulletInteraction.Pierce, gunPierce: BulletInteraction.GunPierce, condition: Boolean,
        incrementCustomPierce: () -> Unit, getCustomPierce: () -> Int
    ): Boolean {
        val accessor = ammo as EntityKineticBulletAccessor
        val ext = ammo as EntityKineticBulletExtension
        if (gunPierce.consume) accessor.pierce -= 1
        if (gunPierce.required && accessor.pierce <= 0) return false
        when (pierce) {
            is BulletInteraction.Pierce.Never -> false
            is BulletInteraction.Pierce.Default -> true
            is BulletInteraction.Pierce.Count -> {
                incrementCustomPierce.invoke()
                getCustomPierce.invoke() < pierce.count
            }
            is BulletInteraction.Pierce.Damage -> ammo.getDamage(result.location) > 0.0F
        } || return false
        if (pierce.conditional && !condition) return false
        ext.`tacztweaks$addDamageModifier`(
            -pierce.damageFalloff,
            pierce.damageMultiplier
        )
        return true
    }

    fun calcBlockBreakingDelta(damage: Float, armorIgnore: Double, state: BlockState, level: ServerLevel, pos: BlockPos): Float {
        val player = object : FakePlayer(level, FAKE_PROFILE) {
            override fun getDigSpeed(state: BlockState, pos: BlockPos?): Float =
                super.getDigSpeed(state, pos) + damage

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

    data class ShieldInteractionResult(
        val blockedDamage: Float,
        val durabilityDamage: (Int) -> Int,
        val disableDuration: Int
    )
}