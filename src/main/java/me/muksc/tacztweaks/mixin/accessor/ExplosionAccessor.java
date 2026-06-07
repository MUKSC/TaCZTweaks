package me.muksc.tacztweaks.mixin.accessor;

import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Explosion.class)
public interface ExplosionAccessor {
    @Accessor("x")
    void tacztweaks$setX(double x);

    @Accessor("y")
    void tacztweaks$setY(double y);

    @Accessor("z")
    void tacztweaks$setZ(double z);
}