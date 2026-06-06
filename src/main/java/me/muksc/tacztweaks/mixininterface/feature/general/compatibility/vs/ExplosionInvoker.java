package me.muksc.tacztweaks.mixininterface.feature.general.compatibility.vs;

import net.minecraft.world.level.Explosion;

public interface ExplosionInvoker {
    static ExplosionInvoker of(Explosion instance) {
        return (ExplosionInvoker) instance;
    }

    void tacztweaks$invokeDoExplodeForce();
}