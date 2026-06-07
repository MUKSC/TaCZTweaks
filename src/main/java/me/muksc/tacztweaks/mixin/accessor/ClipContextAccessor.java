package me.muksc.tacztweaks.mixin.accessor;

import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClipContext.class)
public interface ClipContextAccessor {
    @Accessor("block")
    ClipContext.Block tacztweaks$getBlock();

    @Accessor("fluid")
    ClipContext.Fluid tacztweaks$getFluid();

    @Accessor("collisionContext")
    CollisionContext tacztweaks$getCollisionContext();
}