package me.muksc.tacztweaks.feature.gameplay.behaviour

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.vehicle.Boat

fun Entity.isRowing(): Boolean {
    val boat = controlledVehicle as? Boat ?: return false
    return boat.getPaddleState(0) || boat.getPaddleState(1)
}