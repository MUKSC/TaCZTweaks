package me.muksc.tacztweaks.compat.firstaid

import com.tacz.guns.api.entity.IGunOperator
import ichttt.mods.firstaid.api.enums.EnumPlayerPart
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

/**
 * Utility for converting world-space hit vectors to player-local coordinate space.
 * Handles player rotation and posture (standing/crawling).
 */
object CoordinateTransform {

    /**
     * Converts a world-space hit position to player-local coordinates.
     * @param worldHit The hit position in world coordinates
     * @param player The player being hit
     * @return The hit position in local (player-relative) coordinates
     */
    fun worldToLocal(worldHit: Vec3, player: Player): Vec3 {
        // Step 1: Translate to player-relative coordinates
        val relative = worldHit.subtract(player.position())

        // Step 2: Rotate by inverse player yaw to align with local axes
        val yaw = -player.yRot * Mth.DEG_TO_RAD
        val cos = Mth.cos(yaw)
        val sin = Mth.sin(yaw)

        val localX = relative.x * cos - relative.z * sin
        val localZ = relative.x * sin + relative.z * cos

        // Step 3: Apply crawl rotation if player is prone
        return if (isCrawling(player)) {
            rotateCrawl(Vec3(localX, relative.y, localZ))
        } else {
            Vec3(localX, relative.y, localZ)
        }
    }

    /**
     * Checks if the player is in a crawling/prone state.
     * @param player The player to check
     * @return True if crawling
     */
    private fun isCrawling(player: Player): Boolean {
        val operator = IGunOperator.fromLivingEntity(player)
        return operator?.dataHolder?.isCrawling == true
    }

    /**
     * Rotates the local coordinates 90 degrees around the X-axis for crawling players.
     * This transforms "up" (Y) into "forward" (Z) and vice versa.
     * @param local The local coordinates for a standing player
     * @return The adjusted local coordinates for a crawling player
     */
    private fun rotateCrawl(local: Vec3): Vec3 {
        // Rotate 90 degrees around X-axis: Y becomes -Z, Z becomes Y
        return Vec3(local.x, local.z, -local.y)
    }

    /**
     * Gets the center position of a bodypart in world coordinates.
     * @param player The player
     * @param part The bodypart
     * @return World position of the bodypart center
     */
    fun getPartWorldPosition(player: Player, part: EnumPlayerPart): Vec3 {
        val localCenter = BodypartHitbox.getPartCenter(part)
        return localToWorld(localCenter, player)
    }

    /**
     * Converts local coordinates back to world space.
     * @param local Local coordinates
     * @param player The player
     * @return World coordinates
     */
    fun localToWorld(local: Vec3, player: Player): Vec3 {
        // Reverse crawl rotation if needed
        val adjusted = if (isCrawling(player)) {
            Vec3(local.x, -local.z, local.y)
        } else {
            local
        }

        // Rotate by player yaw
        val yaw = player.yRot * Mth.DEG_TO_RAD
        val cos = Mth.cos(yaw)
        val sin = Mth.sin(yaw)

        val worldX = adjusted.x * cos - adjusted.z * sin
        val worldZ = adjusted.x * sin + adjusted.z * cos

        // Translate to world position
        return player.position().add(worldX, adjusted.y, worldZ)
    }
}
