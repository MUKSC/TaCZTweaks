package me.muksc.tacztweaks.feature.destroy_progress

import com.mojang.authlib.GameProfile
import me.muksc.tacztweaks.mixininterface.feature.datapack.bullet_interactions.DestroySpeedModifiableBlock
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import java.util.UUID
import kotlin.math.exp

//? if fabric {
/*import net.fabricmc.fabric.api.entity.FakePlayer
*///?} else if forge {
import net.minecraftforge.common.util.FakePlayer
//?} else if neoforge {
/*import net.neoforged.neoforge.common.util.FakePlayer
*///?}

private val FAKE_PROFILE = GameProfile(UUID.fromString("BF8411E4-9730-4215-9AE8-1688EEDF9B72"), "[Minecraft]")

fun calculateDestroyProgressDelta(
    damage: Float,
    armorIgnore: Double,
    level: ServerLevel,
    pos: BlockPos,
    state: BlockState
): Float {
    val ext = DestroySpeedModifiableBlock.of(state.block)
    val player = object : FakePlayer(level, FAKE_PROFILE) {
        //? if fabric {
        /*override fun getDestroySpeed(state: BlockState): Float =
            super.getDestroySpeed(state) + damage
        *///?} else {
        override fun getDigSpeed(state: BlockState, pos: BlockPos?): Float =
            super.getDigSpeed(state, pos) + damage
        //?}

        @Suppress("OVERRIDE_DEPRECATION")
        override fun hasCorrectToolForDrops(pState: BlockState): Boolean = true
    }
    return try {
        ext.`tacztweaks$setDestroySpeedMultiplier`(remapArmorIgnore(armorIgnore))
        state.getDestroyProgress(player, level, pos)
    } finally {
        ext.`tacztweaks$setDestroySpeedMultiplier`(1.0F)
    }
}

private fun remapArmorIgnore(armorIgnore: Double): Float =
    exp(-2 * armorIgnore).toFloat()