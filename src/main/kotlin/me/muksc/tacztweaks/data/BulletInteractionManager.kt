package me.muksc.tacztweaks.data

import com.google.common.collect.ImmutableMap
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.mojang.authlib.GameProfile
import com.mojang.logging.LogUtils
import com.mojang.serialization.JsonOps
import com.tacz.guns.util.AttachmentDataUtils
import me.muksc.tacztweaks.BlockBreakingManager
import me.muksc.tacztweaks.Context
import me.muksc.tacztweaks.DestroySpeedModifierHolder
import me.muksc.tacztweaks.EntityKineticBulletExtension
import me.muksc.tacztweaks.data.BulletInteraction.Pierce.ECondition
import me.muksc.tacztweaks.mixin.accessor.EntityKineticBulletAccessor
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.util.FakePlayer
import java.util.*
import kotlin.math.exp

private val GSON = GsonBuilder()
    .setPrettyPrinting()
    .disableHtmlEscaping()
    .create()

object BulletInteractionManager : SimpleJsonResourceReloadListener(GSON, "bullet_interactions") {
    private val LOGGER = LogUtils.getLogger()
    private val FAKE_PROFILE = GameProfile(UUID.fromString("BF8411E4-9730-4215-9AE8-1688EEDF9B72"), "[Minecraft]")
    private var bulletInteractions: Map<ResourceLocation, BulletInteraction> = emptyMap()

    override fun apply(
        map: Map<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profileFiller: ProfilerFiller,
    ) {
        val bulletInteractions = mutableMapOf<ResourceLocation, BulletInteraction>()
        for ((resourceLocation, element) in map) {
            try {
                val bulletInteraction = BulletInteraction.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow(true, LOGGER::error)
                bulletInteractions[resourceLocation] = bulletInteraction
            } catch (e: JsonParseException) {
                LOGGER.error("Parsing error loading bullet interaction $resourceLocation $e")
            }
        }
        this.bulletInteractions = ImmutableMap.copyOf(bulletInteractions)
    }

    fun getInteraction(state: BlockState, gunId: ResourceLocation?): BulletInteraction? =
        bulletInteractions.values.firstOrNull { interaction ->
            interaction.guns.contains(gunId) && interaction.blocks.any { it.test(state) }
        } ?: bulletInteractions.values.firstOrNull { interaction ->
            interaction.guns.isEmpty() && interaction.blocks.any { it.test(state) }
        }

    /**
     * @return whether to pierce the block or not
     */
    fun handleInteraction(level: ServerLevel, state: BlockState, pos: BlockPos, worldPos: BlockPos?): Boolean {
        val ammo = Context.ammo
        val ext = ammo as EntityKineticBulletExtension
        val gun = Context.Gun(ext.`tacztweaks$getGunStack`())

        val interaction = getInteraction(state, gun.id) ?: return false
        val armorIgnore = AttachmentDataUtils.getArmorIgnoreWithAttachment(gun.stack, gun.index?.gunData)

        val breakBlock = when (interaction.blockBreak) {
            is BulletInteraction.BlockBreak.Never -> { false }
            is BulletInteraction.BlockBreak.Count -> BlockBreakingManager.addCurrentProgress(level, pos, 1.0F / interaction.blockBreak.count) >= 1.0F
            is BulletInteraction.BlockBreak.FixedDamage -> run {
                val damage = interaction.blockBreak.damage
                val delta = calcBlockBreakingDelta(damage, armorIgnore, state, level, pos)
                if (delta >= 1.0F) return@run true
                interaction.blockBreak.accumulate && BlockBreakingManager.addCurrentProgress(level, pos, delta) >= 1.0F
            }
            is BulletInteraction.BlockBreak.DynamicDamage -> run {
                val damage = interaction.blockBreak.run { (ammo.getDamage((worldPos ?: pos).center) + modifier) * multiplier }
                val delta = calcBlockBreakingDelta(damage, armorIgnore, state, level, pos)
                if (delta >= 1.0F) return@run true
                interaction.blockBreak.accumulate && BlockBreakingManager.addCurrentProgress(level, pos, delta) >= 1.0F
            }
        }
        if (breakBlock) level.destroyBlock(pos, interaction.drop, ammo.owner)

        val accessor = ammo as EntityKineticBulletAccessor
        return when (interaction.pierce) {
            is BulletInteraction.Pierce.Never -> { false }
            is BulletInteraction.Pierce.Count -> {
                var pierce = ext.`tacztweaks$getBlockPierce`() < interaction.pierce.count && (!interaction.pierce.requireGunPierce || accessor.pierce > 0)
                if (interaction.pierce.condition == ECondition.ON_BREAK) pierce = pierce && breakBlock
                if (pierce) {
                    ext.`tacztweaks$setBlockPierce`(ext.`tacztweaks$getBlockPierce`() + 1)
                    ext.`tacztweaks$setFlatDamageModifier`(ext.`tacztweaks$getFlatDamageModifier`() - interaction.pierce.damageFalloff)
                    ext.`tacztweaks$setDamageMultiplier`(ext.`tacztweaks$getDamageMultiplier`() * interaction.pierce.damageMultiplier)
                }
                pierce
            }
            is BulletInteraction.Pierce.Damage -> {
                var pierce = !interaction.pierce.requireGunPierce || accessor.pierce > 0
                if (interaction.pierce.condition == ECondition.ON_BREAK) pierce = pierce && breakBlock
                if (pierce) {
                    ext.`tacztweaks$setFlatDamageModifier`(ext.`tacztweaks$getFlatDamageModifier`() - interaction.pierce.damageFalloff)
                    ext.`tacztweaks$setDamageMultiplier`(ext.`tacztweaks$getDamageMultiplier`() * interaction.pierce.damageMultiplier)
                }
                pierce && ammo.getDamage((worldPos ?: pos).center) > 0.0F
            }
        }
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
}