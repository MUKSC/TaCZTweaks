package me.muksc.tacztweaks.compat.firstaid

import com.mojang.serialization.Codec
import ichttt.mods.firstaid.FirstAid
import ichttt.mods.firstaid.FirstAidConfig
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel
import ichttt.mods.firstaid.api.distribution.IDamageDistributionAlgorithm
import ichttt.mods.firstaid.api.enums.EnumPlayerPart
import ichttt.mods.firstaid.common.network.MessageUpdatePart
import ichttt.mods.firstaid.common.util.ArmorUtils
import ichttt.mods.firstaid.common.util.CommonUtils
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.PacketDistributor

/**
 * Distributes explosion damage based on proximity of bodyparts to the blast center.
 * Uses distance-based falloff for realistic explosion damage.
 */
class ExplosionDistributor(private val explosionCenter: Vec3) : IDamageDistributionAlgorithm {

    companion object {
        val CODEC: Codec<ExplosionDistributor> = Codec.unit { ExplosionDistributor(Vec3.ZERO) }
    }

    override fun distributeDamage(
        totalDamage: Float,
        player: Player,
        source: DamageSource,
        addStat: Boolean
    ): Float {
        val damageModel = CommonUtils.getDamageModel(player) ?: return totalDamage

        // Calculate distances from explosion center to all bodypart centers
        val distances = EnumPlayerPart.entries.associateWith { part ->
            val partWorldPos = CoordinateTransform.getPartWorldPosition(player, part)
            partWorldPos.distanceToSqr(explosionCenter)
        }

        // Sort parts by proximity (closest first)
        val sortedParts = distances.entries.sortedBy { it.value }

        // Distribute damage with falloff
        // Closest: 50%, Second: 25%, Third: 15%, Rest: 10% split
        if (sortedParts.isNotEmpty()) {
            applyExplosionDamage(damageModel, player, source, sortedParts[0].key, totalDamage * 0.50f, addStat)
        }
        if (sortedParts.size >= 2) {
            applyExplosionDamage(damageModel, player, source, sortedParts[1].key, totalDamage * 0.25f, addStat)
        }
        if (sortedParts.size >= 3) {
            applyExplosionDamage(damageModel, player, source, sortedParts[2].key, totalDamage * 0.15f, addStat)
        }
        // Distribute remaining 10% among other parts
        if (sortedParts.size > 3) {
            val remainingDamage = totalDamage * 0.10f
            val perPart = remainingDamage / (sortedParts.size - 3)
            for (i in 3 until sortedParts.size) {
                applyExplosionDamage(damageModel, player, source, sortedParts[i].key, perPart, addStat)
            }
        }

        // Return 0 because we handled all damage components with our falloff distribution
        return 0f
    }

    /**
     * Applies explosion damage to a specific part with armor absorption and spillover support.
     */
    private fun applyExplosionDamage(
        damageModel: AbstractPlayerDamageModel,
        player: Player,
        source: DamageSource,
        partEnum: EnumPlayerPart,
        damage: Float,
        addStat: Boolean
    ) {
        var currentDamage = damage
        val armorSlot = getMappedSlot(partEnum)

        // Apply armor absorption
        currentDamage = ArmorUtils.applyArmor(player, player.getItemBySlot(armorSlot), source, currentDamage, armorSlot)
        if (currentDamage <= 0F) return

        // Apply enchantment modifiers
        currentDamage = ArmorUtils.applyEnchantmentModifiers(player, armorSlot, source, currentDamage)
        if (currentDamage <= 0F) return

        val part = damageModel.getFromEnum(partEnum)
        val minHealth = if (part.canCauseDeath && FirstAidConfig.SERVER.useFriendlyRandomDistribution.get()) 1.0f else 0f

        val undelivered = part.damage(currentDamage, player, addStat, minHealth)

        // Sync to client
        if (player is ServerPlayer) {
            FirstAid.NETWORKING.send(PacketDistributor.PLAYER.with { player }, MessageUpdatePart(part))
        }

        if (undelivered > 0 && partEnum != EnumPlayerPart.BODY && partEnum != EnumPlayerPart.HEAD) {
            // Apply spillover to body (1.0 ratio for explosions)
            SpilloverHandler.applySpillover(player, partEnum, undelivered, true)
        }
    }

    /**
     * Maps a body part to an equipment slot for armor protection calculation.
     */
    private fun getMappedSlot(part: EnumPlayerPart): EquipmentSlot = when (part) {
        EnumPlayerPart.HEAD -> EquipmentSlot.HEAD
        EnumPlayerPart.BODY, EnumPlayerPart.LEFT_ARM, EnumPlayerPart.RIGHT_ARM -> EquipmentSlot.CHEST
        EnumPlayerPart.LEFT_LEG, EnumPlayerPart.RIGHT_LEG -> EquipmentSlot.LEGS
        EnumPlayerPart.LEFT_FOOT, EnumPlayerPart.RIGHT_FOOT -> EquipmentSlot.FEET
    }

    override fun codec(): Codec<out IDamageDistributionAlgorithm> = CODEC
}
