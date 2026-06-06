package me.muksc.tacztweaks.mixin.feature.general.compatibility.vs;

import me.muksc.tacztweaks.mixininterface.feature.general.compatibility.vs.ExplosionInvoker;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Explosion.class, priority = 1500, remap = false)
public class MixinExplosionMixin implements ExplosionInvoker {
    @SuppressWarnings("target")
    @Dynamic
    @Shadow
    private void doExplodeForce() {
        throw new AssertionError();
    }

    @Override
    public void tacztweaks$invokeDoExplodeForce() {
        doExplodeForce();
    }
}