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
 * Custom damage distribution algorithm for TacZ bullets.
 * Uses precise 3D hitbox detection instead of FirstAid's default random/height-based distribution.
 */
class TacZDamageDistribution(private val hitLocation: Vec3) : IDamageDistributionAlgorithm {

    companion object {
        val CODEC: Codec<TacZDamageDistribution> = Codec.unit { TacZDamageDistribution(Vec3.ZERO) }
    }

    override fun distributeDamage(
        damage: Float,
        player: Player,
        source: DamageSource,
        addStat: Boolean
    ): Float {
        val damageModel = CommonUtils.getDamageModel(player) ?: return damage
        var currentDamage = damage

        // Convert world hit to local coordinates
        val localHit = CoordinateTransform.worldToLocal(hitLocation, player)

        // Determine which bodypart was hit, fallback to closest part
        val hitPart = BodypartHitbox.getHitPart(localHit) ?: BodypartHitbox.getClosestPart(localHit)

        // Determine armor slot based on custom mapping
        val armorSlot = getMappedSlot(hitPart)

        // Apply armor absorption
        currentDamage = ArmorUtils.applyArmor(player, player.getItemBySlot(armorSlot), source, currentDamage, armorSlot)
        if (currentDamage <= 0F) return 0F

        // Apply enchantment modifiers
        currentDamage = ArmorUtils.applyEnchantmentModifiers(player, armorSlot, source, currentDamage)
        if (currentDamage <= 0F) return 0F

        // Apply damage to the specific part
        val part = damageModel.getFromEnum(hitPart)

        // FirstAid often leaves 1HP if configuration allows
        val minHealth = if (part.canCauseDeath && FirstAidConfig.SERVER.useFriendlyRandomDistribution.get()) 1.0f else 0f

        val undelivered = part.damage(currentDamage, player, addStat, minHealth)

        // Sync to client
        if (player is ServerPlayer) {
            FirstAid.NETWORKING.send(PacketDistributor.PLAYER.with { player }, MessageUpdatePart(part))
        }

        if (undelivered > 0 && hitPart != EnumPlayerPart.BODY && hitPart != EnumPlayerPart.HEAD) {
            // Apply spillover to body
            SpilloverHandler.applySpillover(player, hitPart, undelivered, false)
        }

        // We return 0 as we've already handled the "excess" via spillover logic
        return 0f
    }

    /**
     * Maps a body part to an equipment slot for armor protection calculation,
     * following the user's specific protection requirements.
     */
    private fun getMappedSlot(part: EnumPlayerPart): EquipmentSlot = when (part) {
        EnumPlayerPart.HEAD -> EquipmentSlot.HEAD
        EnumPlayerPart.BODY, EnumPlayerPart.LEFT_ARM, EnumPlayerPart.RIGHT_ARM -> EquipmentSlot.CHEST
        EnumPlayerPart.LEFT_LEG, EnumPlayerPart.RIGHT_LEG -> EquipmentSlot.LEGS
        EnumPlayerPart.LEFT_FOOT, EnumPlayerPart.RIGHT_FOOT -> EquipmentSlot.FEET
    }

    override fun codec(): Codec<out IDamageDistributionAlgorithm> = CODEC
}
