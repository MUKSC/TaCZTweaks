package me.muksc.tacztweaks.mixin.compat.vs.client;

import me.muksc.tacztweaks.mixininterface.compat.vs.ParticleWithShip;
import net.minecraft.client.particle.Particle;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.core.api.ships.ClientShip;

import javax.annotation.Nullable;

@Mixin(Particle.class)
public abstract class ParticleMixin implements ParticleWithShip {
    @Unique
    private ClientShip tacztweaks$ship = null;

    @Unique
    private Vector3d tacztweaks$shipPos = null;

    @Override
    public @Nullable ClientShip tacztweaks$getShip() {
        return tacztweaks$ship;
    }

    @Override
    public void tacztweaks$setShip(ClientShip ship) {
        tacztweaks$ship = ship;
    }

    @Override
    public @Nullable Vector3d tacztweaks$getShipPos() {
        return tacztweaks$shipPos;
    }

    @Override
    public void tacztweaks$setShipPos(Vector3d pos) {
        tacztweaks$shipPos = pos;
    }
}
