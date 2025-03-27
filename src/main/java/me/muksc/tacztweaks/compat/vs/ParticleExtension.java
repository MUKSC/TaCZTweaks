package me.muksc.tacztweaks.compat.vs;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ClientShip;

import javax.annotation.Nullable;

public interface ParticleExtension {
    @Nullable ClientShip tacztweaks$getShip();

    void tacztweaks$setShip(ClientShip ship);

    @Nullable Vector3d tacztweaks$getShipPos();

    void tacztweaks$setShipPos(Vector3d pos);
}
