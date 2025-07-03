package me.muksc.tacztweaks.mixininterface.compat.vs;

import org.valkyrienskies.core.api.ships.Ship;

import javax.annotation.Nullable;

public interface BlockHitResultWithShip {
    @Nullable
    Ship tacztweaks$getShip();

    void tacztweaks$setShip(Ship ship);
}
