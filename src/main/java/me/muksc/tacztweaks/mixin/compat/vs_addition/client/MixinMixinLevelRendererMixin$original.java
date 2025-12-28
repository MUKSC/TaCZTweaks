package me.muksc.tacztweaks.mixin.compat.vs_addition.client;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.mixininterface.compat.vs.ParticleWithShip;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.ClientShip;

@Mixin(value = LevelRenderer.class, priority = 1500, remap = false)
public abstract class MixinMixinLevelRendererMixin$original {
    @TargetHandler(
        mixin = "forge.io.github.xiewuzhiying.vs_addition.mixin.valkyrienskies.client.MixinMixinLevelRenderer",
        name = "spawnParticleInWorld"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/llamalad7/mixinextras/injector/wrapoperation/Operation;call([Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1))
    private <R> R tacztweaks$spawnParticleInWorld$setShip(
        R original,
        @Local(argsOnly = true, ordinal = 0) double x,
        @Local(argsOnly = true, ordinal = 1) double y,
        @Local(argsOnly = true, ordinal = 2) double z,
        @Local ClientShip ship
    ) {
        if (!Config.Compat.INSTANCE.vsCollisionCompat()) return original;
        if (!(original instanceof Particle)) return original;
        ParticleWithShip ext = (ParticleWithShip) original;
        ext.tacztweaks$setShip(ship);
        ext.tacztweaks$setShipPos(new Vector3d(x, y, z));
        return original;
    }
}
