package me.muksc.tacztweaks.mixin.compat.vs.client;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.mixininterface.compat.vs.ParticleWithShip;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.ClientShip;

@Mixin(value = LevelRenderer.class, priority = 1500, remap = false)
public abstract class MixinLevelRendererMixin {
    @Dynamic
    @TargetHandler(
        mixin = "org.valkyrienskies.mod.mixin.feature.transform_particles.MixinLevelRenderer",
        name = "spawnParticleInWorld"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", remap = true))
    private Particle tacztweaks$spawnParticleInWorld$setShip(
        Particle original,
        @Local(argsOnly = true, ordinal = 0) double x,
        @Local(argsOnly = true, ordinal = 1) double y,
        @Local(argsOnly = true, ordinal = 2) double z,
        @Local ClientShip ship
    ) {
        if (!Config.Compat.INSTANCE.vsCollisionCompat()) return original;
        if (original == null) return original;
        ParticleWithShip ext = (ParticleWithShip) original;
        ext.tacztweaks$setShip(ship);
        ext.tacztweaks$setShipPos(new Vector3d(x, y, z));
        return original;
    }
}
