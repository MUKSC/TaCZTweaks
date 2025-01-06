package me.muksc.tacztweaks.mixin.compat.vs;

import me.muksc.tacztweaks.compat.vs.ExplosionInvoker;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Explosion.class, remap = false)
public abstract class ExplosionMixin implements ExplosionInvoker {
    @SuppressWarnings("MissingUnique")
    private void doExplodeForce() {
        throw new AssertionError();
    }

    @Override
    public void tacztweaks$invokeDoExplodeForce() {
        doExplodeForce();
    }
}
