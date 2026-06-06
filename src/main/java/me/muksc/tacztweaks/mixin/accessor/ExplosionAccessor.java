package me.muksc.tacztweaks.mixin.accessor;

import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Explosion.class)
public interface ExplosionAccessor {
    @Accessor
    void setX(double x);

    @Accessor
    void setY(double y);

    @Accessor
    void setZ(double z);
}