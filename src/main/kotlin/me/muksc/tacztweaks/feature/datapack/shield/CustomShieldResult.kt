package me.muksc.tacztweaks.feature.datapack.shield

import java.util.function.Function

sealed interface CustomShieldResult {
    @JvmRecord
    data class Blocked(
        val damage: Float,
        val knockback: Boolean,
        val durabilityDamage: Function<Float, Float>,
        val disableDuration: Int
    ) : CustomShieldResult

    object Bypass : CustomShieldResult
}