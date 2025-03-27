package me.muksc.tacztweaks.compat.vs;

import org.valkyrienskies.core.api.ships.Ship;

import javax.annotation.Nullable;

public interface BlockHitResultExtension {
    @Nullable
    Ship tacztweaks$getShip();

    void tacztweaks$setShip(Ship ship);
}
