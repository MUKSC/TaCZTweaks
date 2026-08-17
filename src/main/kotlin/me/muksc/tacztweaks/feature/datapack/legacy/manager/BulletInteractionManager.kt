package me.muksc.tacztweaks.feature.datapack.legacy.manager

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import com.tacz.guns.entity.EntityKineticBullet
import com.tacz.guns.particles.BulletHoleOption
import com.tacz.guns.util.AttachmentDataUtils
import com.tacz.guns.util.TacHitResult
import java.util.Optional
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.Config
import me.muksc.tacztweaks.core.anyOrEmpty
import me.muksc.tacztweaks.core.resource.IdentifiableResourceReloadListener
import me.muksc.tacztweaks.core.tacz.GunStack
import me.muksc.tacztweaks.feature.datapack.legacy.BulletInteraction
import me.muksc.tacztweaks.feature.destroy_progress.DestroyProgressManager
import me.muksc.tacztweaks.feature.destroy_progress.calculateDestroyProgressDelta
import me.muksc.tacztweaks.feature.raytracer.BulletHandler.InteractionResult
import me.muksc.tacztweaks.feature.datapack.shield.CustomShieldResult
import me.muksc.tacztweaks.mixin.accessor.EntityKineticBulletAccessor
import me.muksc.tacztweaks.mixininterface.feature.datapack.TaCZTweaksBullet
import me.muksc.tacztweaks.mixininterop.*
//? if neoforge {
import dev.ryanhcode.sable.Sable
import dev.ryanhcode.sable.sublevel.SubLevel
//?}
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3

//? if <1.20.5 {
import me.muksc.tacztweaks.core.extension.getOrThrow
import me.muksc.tacztweaks.core.extension.test
//?}
//? if fabric {
/*import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
*///?} else if forge {
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.level.BlockEvent
//?} else if neoforge {
/*import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.level.BlockEvent
*///?}

private val COMPARATOR = compareBy<BulletInteraction> { it.priority }
    .thenByDescending { it.target.isNotEmpty() }
    .thenByDescending { when (it) {
        is BulletInteraction.Block -> it.blocks.isNotEmpty()
        is BulletInteraction.Entity -> it.entities.isNotEmpty()
        is BulletInteraction.Shield -> it.predicate != null
    } }

object BulletInteractionManager : BaseDataManager<BulletInteraction>("bullet_interactions", COMPARATOR), IdentifiableResourceReloadListener {
    private val ID = TaCZTweaks.id("bullet_interactions")
    override fun id(): ResourceLocation = ID

    override val debugEnabled: Boolean get() = Config.General.Debug.bulletInteractions()

    override fun parseElement(json: JsonElement): BulletInteraction =
        BulletInteraction.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()

    private val INTERACTION_BLOCK_DEFAULT = BulletInteraction.Block(
        target = emptyList(), blocks = emptyList(),
        blockBreak = BulletInteraction.Block.BlockBreak.Never,
        pierce = BulletInteraction.Pierce.Never,
        gunPierce = BulletInteraction.GunPierce(false, false),
        priority =  0
    )
    private val INTERACTION_ENTITY_DEFAULT = BulletInteraction.Entity(
        target = emptyList(), entities = emptyList(),
        damage = BulletInteraction.Entity.EntityDamage(0.0F, 1.0F),
        pierce = BulletInteraction.Pierce.Default(false, 0.0F, 1.0F, false),
        gunPierce = BulletInteraction.GunPierce(true, true),
        priority = 0
    )
    private val INTERACTION_SHIELD_DEFAULT = BulletInteraction.Shield(
        target = emptyList(), predicate = Optional.empty(),
        damage = BulletInteraction.Shield.ShieldDamage(0.0F, 1.0F),
        disable = BulletInteraction.Shield.Disable(0, 0.0F, true),
        durability = BulletInteraction.Shield.Durability.FixedDamage(0.0F, true),
        priority = 0
    )

    private inline fun <reified T : BulletInteraction, E> getBulletInteraction(
        entity: EntityKineticBullet,
        location: Vec3,
        selector: (T) -> List<E>,
        predicate: (E) -> Boolean
    ): Pair<ResourceLocation, T>? = byType<T>().entries.firstOrNull { (_, interaction) ->
        interaction.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(location)) }
            && selector(interaction).anyOrEmpty(predicate)
    }?.toPair()

    private inline fun <reified T : BulletInteraction> getBulletInteraction(
        entity: EntityKineticBullet,
        location: Vec3,
        predicate: (T) -> Boolean
    ): Pair<ResourceLocation, T>? = byType<T>().entries.firstOrNull { (_, interaction) ->
        interaction.target.anyOrEmpty { it.test(entity, entity.gunId, entity.getDamage(location)) }
            && predicate(interaction)
    }?.toPair()

    fun handleBlockInteraction(bullet: EntityKineticBullet, result: BlockHitResult, state: BlockState): InteractionResult {
        val level = bullet.level() as? ServerLevel ?: return InteractionResult(false, false)
        val pos = result.blockPos.immutable()
        val ext = TaCZTweaksBullet.of(bullet)
        val stack = GunStack(ext.gunStack)
        val (id, interaction) = getBulletInteraction(bullet, result.location, BulletInteraction.Block::blocks) {
            it.test(level, pos, state)
        } ?: (TaCZTweaks.id("default") to INTERACTION_BLOCK_DEFAULT)
        logger.infoDebug("Using block bullet interaction: $id")

        val armorIgnore = run {
            val gunData = stack.index?.gunData ?: return@run 0.0
            AttachmentDataUtils.getArmorIgnoreWithAttachment(stack.stack, gunData)
        }
        val destroyBlock = shouldDestroyBlock(
            interaction.blockBreak, armorIgnore,
            state, level, pos
        ) { bullet.getDamage(result.location) }
        if (destroyBlock) destroyBlock(interaction.blockBreak, bullet.owner, state, level, pos)
        val pierce = shouldPierce(
            bullet, result,
            interaction.pierce, interaction.gunPierce,destroyBlock,
            incrementCustomPierce = { ext.blockPierce += 1 },
            getCustomPierce = ext::blockPierce
        )
        if (pierce && !destroyBlock && interaction.pierce.renderBulletHole) {
            val option = BulletHoleOption(
                result.direction, pos,
                bullet.ammoId.toString(),
                bullet.gunId.toString(),
                bullet.gunDisplayId.toString()
            )
            level.sendParticles(
                option,
                result.location.x, result.location.y, result.location.z,
                1,
                0.0, 0.0, 0.0,
                0.0
            )
        }
        return InteractionResult(pierce, destroyBlock)
    }

    fun handleEntityInteraction(bullet: EntityKineticBullet, result: TacHitResult, context: ClipContext): InteractionResult {
        val accessor = bullet as EntityKineticBulletAccessor
        val ext = TaCZTweaksBullet.of(bullet)
        val entity = result.entity
        val (id, interaction) = getBulletInteraction(bullet, result.location, BulletInteraction.Entity::entities) {
            it.test(entity)
        } ?: (TaCZTweaks.id("default") to INTERACTION_ENTITY_DEFAULT)
        logger.infoDebug("Using entity bullet interaction: $id")

        ext.modifyEntityHitDamage(
            interaction.damage.modifier,
            interaction.damage.multiplier
        )
        accessor.onHitEntity(result, context.from, result.location)

        val isDead = !entity.isAlive
        if (accessor.explosion) return InteractionResult(false, isDead)
        val pierce = shouldPierce(
            bullet, result,
            interaction.pierce,interaction.gunPierce, isDead,
            incrementCustomPierce = { ext.entityPierce += 1 },
            getCustomPierce = ext::entityPierce
        )
        return InteractionResult(pierce, isDead)
    }

    fun handleShieldInteraction(bullet: EntityKineticBullet, location: Vec3, shield: ItemStack, originalDamage: Float): CustomShieldResult {
        val (id, interaction) = getBulletInteraction<BulletInteraction.Shield>(bullet, location) {
            it.predicate == null || it.predicate.test(shield)
        } ?: (TaCZTweaks.id("default") to INTERACTION_SHIELD_DEFAULT)
        logger.infoDebug("Using shield bullet interaction: $id")

        val damage = (originalDamage - interaction.damage.falloff) * interaction.damage.multiplier
        val durabilityDamage = lambda@ { durabilityDamage: Float ->
            if (interaction.durability.conditional && damage <= 0.0F) return@lambda 0.0F
            when (interaction.durability) {
                is BulletInteraction.Shield.Durability.DynamicDamage -> interaction.durability.run {
                    (durabilityDamage + modifier) * multiplier
                }
                is BulletInteraction.Shield.Durability.FixedDamage -> interaction.durability.damage
            }
        }
        val disableDuration = run {
            if (interaction.disable.conditional && damage <= 0) return@run 0
            if (bullet.random.nextFloat() < interaction.disable.chance) {
                interaction.disable.duration
            } else {
                0
            }
        }
        return CustomShieldResult.Blocked(damage, false, durabilityDamage, disableDuration)
    }

    fun shouldDestroyBlock(
        blockBreak: BulletInteraction.Block.BlockBreak, armorIgnore: Double,
        state: BlockState, level: ServerLevel, pos: BlockPos,
        getDamage: () -> Float
    ): Boolean = when (blockBreak) {
        is BulletInteraction.Block.BlockBreak.Never -> false
        is BulletInteraction.Block.BlockBreak.Instant -> true
        is BulletInteraction.Block.BlockBreak.Count -> DestroyProgressManager.addCurrentProgress(
            level, pos, 1.0F / blockBreak.count
        )
        is BulletInteraction.Block.BlockBreak.FixedDamage -> {
            val damage = blockBreak.damage
            val delta = calculateDestroyProgressDelta(damage, armorIgnore, level, pos, state)
            if (blockBreak.accumulate) {
                DestroyProgressManager.addCurrentProgress(level, pos, delta)
            } else {
                delta >= 1.0F
            }
        }
        is BulletInteraction.Block.BlockBreak.DynamicDamage -> {
            val damage = blockBreak.run { (getDamage() + modifier) * multiplier }
            val delta = calculateDestroyProgressDelta(damage, armorIgnore, level, pos, state)
            if (blockBreak.accumulate) {
                DestroyProgressManager.addCurrentProgress(level, pos, delta)
            } else {
                delta >= 1.0F
            }
        }
    }

    fun destroyBlock(
        blockBreak: BulletInteraction.Block.BlockBreak,
        entity: Entity?, state: BlockState,
        level: ServerLevel, pos: BlockPos
    ) {
        //? if fabric
        //val blockEntity by lazy { level.getBlockEntity(pos) }
        //? if neoforge {
        if (Config.General.Compatibility.sableCompat()) {
            val sableSub = Sable.HELPER.getContaining(level, pos)
            if (sableSub != null) {
                val sableLevel = sableSub.getLevel()
                val localPos = sableSub.logicalPose().transformPositionInverse(pos.getCenter())
                val homePos = BlockPos.containing(localPos)
                if (sableLevel is ServerLevel && !sableLevel.getBlockState(homePos).isAir) {
                    sableLevel.destroyBlock(homePos, blockBreak.drop, entity)
                    return
                }
            }
        }
        //?}
        if (entity is ServerPlayer) {
            //? if fabric {
            /*if (!PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, entity, pos, state, blockEntity)) {
                PlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(level, entity, pos, state, blockEntity)
                return
            }
            *///?} else if forge {
            val event = BlockEvent.BreakEvent(level, pos, state, entity)
            if (MinecraftForge.EVENT_BUS.post(event)) return
            //?} else if neoforge {
            /*val event = BlockEvent.BreakEvent(level, pos, state, entity)
            if (NeoForge.EVENT_BUS.post(event).isCanceled) return
            *///?}
        }
        level.destroyBlock(pos, blockBreak.drop, entity)
        //?if fabric {
        /*if (entity is ServerPlayer) {
            PlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(level, entity, pos, state, blockEntity)
        }
        *///?}
        val replaceWith = blockBreak.replaceWith
        if (replaceWith.state.isAir && replaceWith.place(level, pos, Block.UPDATE_CLIENTS)) {
            level.blockUpdated(pos, replaceWith.state.block)
        }
    }

    private fun shouldPierce(
        bullet: EntityKineticBullet, result: HitResult,
        pierce: BulletInteraction.Pierce, gunPierce: BulletInteraction.GunPierce, success: Boolean,
        incrementCustomPierce: () -> Unit, getCustomPierce: () -> Int
    ): Boolean {
        val accessor = bullet as EntityKineticBulletAccessor
        val ext = TaCZTweaksBullet.of(bullet)
        if (gunPierce.consume) accessor.pierce -= 1
        if (gunPierce.required && accessor.pierce <= 0) return false
        when (pierce) {
            is BulletInteraction.Pierce.Never -> false
            is BulletInteraction.Pierce.Default -> true
            is BulletInteraction.Pierce.Count -> {
                incrementCustomPierce()
                getCustomPierce() < pierce.count
            }
            is BulletInteraction.Pierce.Damage -> bullet.getDamage(result.location) > 0.0F
        } || return false
        if (pierce.conditional && !success) return false
        ext.modifyDamage(-pierce.damageFalloff, pierce.damageMultiplier)
        return true
    }
}