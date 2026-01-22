package me.muksc.tacztweaks.compat.firstaid

import ichttt.mods.firstaid.api.enums.EnumPlayerPart
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

/**
 * Defines 3D hitboxes for player bodyparts in local coordinate space.
 * All coordinates are relative to the player's center position with rotation applied.
 */
object BodypartHitbox {

    enum class BodypartSlot(val part: EnumPlayerPart, val armorSlot: EquipmentSlot) {
        HEAD(EnumPlayerPart.HEAD, EquipmentSlot.HEAD),
        BODY(EnumPlayerPart.BODY, EquipmentSlot.CHEST),
        LEFT_ARM(EnumPlayerPart.LEFT_ARM, EquipmentSlot.CHEST),
        RIGHT_ARM(EnumPlayerPart.RIGHT_ARM, EquipmentSlot.CHEST),
        LEFT_LEG(EnumPlayerPart.LEFT_LEG, EquipmentSlot.LEGS),
        RIGHT_LEG(EnumPlayerPart.RIGHT_LEG, EquipmentSlot.LEGS),
        LEFT_FOOT(EnumPlayerPart.LEFT_FOOT, EquipmentSlot.FEET),
        RIGHT_FOOT(EnumPlayerPart.RIGHT_FOOT, EquipmentSlot.FEET);

        companion object {
            fun fromPart(part: EnumPlayerPart): BodypartSlot? = entries.find { it.part == part }
        }
    }

    // Hitbox definitions in local coordinate space (player-relative)
    private val HITBOXES: Map<BodypartSlot, AABB> = mapOf(
        // Head: -0.2 to 0.2 (X), 1.5 to 1.8 (Y), -0.2 to 0.2 (Z)
        BodypartSlot.HEAD to AABB(-0.2, 1.5, -0.2, 0.2, 1.8, 0.2),
        // Body (Torso): -0.2 to 0.2 (X), 0.7 to 1.5 (Y), -0.15 to 0.15 (Z)
        BodypartSlot.BODY to AABB(-0.2, 0.7, -0.15, 0.2, 1.5, 0.15),
        // MIRRORED: Left Arm now on positive X (player's actual left from their perspective)
        BodypartSlot.LEFT_ARM to AABB(0.2, 0.7, -0.15, 0.4, 1.5, 0.15),
        // MIRRORED: Right Arm now on negative X (player's actual right from their perspective)
        BodypartSlot.RIGHT_ARM to AABB(-0.4, 0.7, -0.15, -0.2, 1.5, 0.15),
        // MIRRORED: Left Leg now on positive X
        BodypartSlot.LEFT_LEG to AABB(0.0, 0.15, -0.15, 0.2, 0.7, 0.15),
        // MIRRORED: Right Leg now on negative X
        BodypartSlot.RIGHT_LEG to AABB(-0.2, 0.15, -0.15, 0.0, 0.7, 0.15),
        // MIRRORED: Feet
        BodypartSlot.LEFT_FOOT to AABB(0.0, 0.0, -0.2, 0.2, 0.15, 0.2),
        BodypartSlot.RIGHT_FOOT to AABB(-0.2, 0.0, -0.2, 0.0, 0.15, 0.2)
    )

    // Priority order for hit detection
    private val CHECK_ORDER = arrayOf(
        BodypartSlot.HEAD,
        BodypartSlot.LEFT_ARM,
        BodypartSlot.RIGHT_ARM,
        BodypartSlot.LEFT_LEG,
        BodypartSlot.RIGHT_LEG,
        BodypartSlot.LEFT_FOOT,
        BodypartSlot.RIGHT_FOOT,
        BodypartSlot.BODY
    )

    /**
     * Checks if the given local coordinate point is contained within a bodypart's hitbox.
     * @param localPoint Point in player-local coordinate space
     * @return The bodypart that contains this point, or null if no match
     */
    fun getHitPart(localPoint: Vec3): EnumPlayerPart? {
        for (slot in CHECK_ORDER) {
            val box = HITBOXES[slot] ?: continue
            if (box.contains(localPoint)) {
                return slot.part
            }
        }
        return null
    }

    /**
     * Gets the center point of a bodypart's hitbox in local coordinates.
     * @param part The bodypart
     * @return Center point, or Vec3.ZERO if not found
     */
    fun getPartCenter(part: EnumPlayerPart): Vec3 {
        val slot = BodypartSlot.fromPart(part) ?: return Vec3.ZERO
        val box = HITBOXES[slot] ?: return Vec3.ZERO
        return box.center
    }

    /**
     * Finds the closest bodypart to the given local point.
     * Used as a fallback when precise hit detection fails.
     * @param localPoint Point in local coordinate space
     * @return The closest bodypart
     */
    fun getClosestPart(localPoint: Vec3): EnumPlayerPart {
        var closest = EnumPlayerPart.BODY
        var minDistance = Double.MAX_VALUE

        for (slot in BodypartSlot.entries) {
            val center = getPartCenter(slot.part)
            val distance = center.distanceToSqr(localPoint)
            if (distance < minDistance) {
                minDistance = distance
                closest = slot.part
            }
        }
        return closest
    }
}
