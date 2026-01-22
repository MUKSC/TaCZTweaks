package me.muksc.tacztweaks.compat.firstaid

import ichttt.mods.firstaid.FirstAid
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel
import ichttt.mods.firstaid.api.enums.EnumPlayerPart
import ichttt.mods.firstaid.common.network.MessageUpdatePart
import ichttt.mods.firstaid.common.util.CommonUtils
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraftforge.network.PacketDistributor

/**
 * Handles damage spillover from limbs to torso when limb health is depleted.
 * Calls FirstAid's bodyPart.damage() directly, which bypasses vanilla i-frames.
 */
object SpilloverHandler {

    /**
     * Applies spillover damage from a limb to the torso.
     * @param player The player receiving spillover damage
     * @param sourcePart The bodypart that was originally hit
     * @param remainingDamage The damage that exceeded the limb's health
     * @param isExplosion Whether the damage came from an explosion
     */
    fun applySpillover(
        player: Player,
        sourcePart: EnumPlayerPart,
        remainingDamage: Float,
        isExplosion: Boolean
    ) {
        // Skip spillover for critical parts (they handle death differently)
        if (sourcePart == EnumPlayerPart.HEAD || sourcePart == EnumPlayerPart.BODY) {
            return
        }

        val damageModel = CommonUtils.getDamageModel(player) ?: return

        // Special handling for feet: damage spills to leg first, then to body
        if (sourcePart == EnumPlayerPart.LEFT_FOOT || sourcePart == EnumPlayerPart.RIGHT_FOOT) {
            handleFootSpillover(player, damageModel, sourcePart, remainingDamage)
            return
        }

        // Standard spillover for arms and legs: goes to body
        // Bullets: 0.8 (20% energy loss in flesh), Explosions: 1.0 (shockwave transfers fully)
        val ratio = if (isExplosion) 1.0f else 0.8f
        val spilloverDamage = remainingDamage * ratio

        if (spilloverDamage <= 0) return

        val bodyPart = damageModel.getFromEnum(EnumPlayerPart.BODY)
        bodyPart.damage(spilloverDamage, player, true)
        syncPart(player, damageModel, EnumPlayerPart.BODY)
    }

    /**
     * Handles spillover from feet specifically: 80% to leg, if leg is destroyed then 40% to body.
     */
    private fun handleFootSpillover(
        player: Player,
        damageModel: AbstractPlayerDamageModel,
        footPart: EnumPlayerPart,
        remainingDamage: Float
    ) {
        val legPart = if (footPart == EnumPlayerPart.LEFT_FOOT)
            EnumPlayerPart.LEFT_LEG
        else
            EnumPlayerPart.RIGHT_LEG

        val leg = damageModel.getFromEnum(legPart)
        val legSpillover = remainingDamage * 0.8f

        if (leg.currentHealth > 0) {
            val legUndelivered = leg.damage(legSpillover, player, true)
            syncPart(player, damageModel, legPart)

            if (legUndelivered > 0) {
                applyToBody(player, damageModel, legUndelivered)
            }
        } else {
            // Leg is destroyed, 40% goes to body instead (50% energy loss)
            val bodySpillover = remainingDamage * 0.4f
            applyToBody(player, damageModel, bodySpillover)
        }
    }

    /**
     * Helper to apply damage to body and sync.
     */
    private fun applyToBody(player: Player, damageModel: AbstractPlayerDamageModel, damage: Float) {
        if (damage <= 0) return
        val body = damageModel.getFromEnum(EnumPlayerPart.BODY)
        body.damage(damage, player, true)
        syncPart(player, damageModel, EnumPlayerPart.BODY)
    }

    /**
     * Syncs a specific part to the client.
     */
    private fun syncPart(player: Player, damageModel: AbstractPlayerDamageModel, part: EnumPlayerPart) {
        if (player is ServerPlayer) {
            val partInstance = damageModel.getFromEnum(part)
            FirstAid.NETWORKING.send(
                PacketDistributor.PLAYER.with { player },
                MessageUpdatePart(partInstance)
            )
        }
    }
}
