package me.muksc.tacztweaks.mixin.compat.vs;

import me.muksc.tacztweaks.mixininterface.compat.vs.BlockHitResultWithShip;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.core.api.ships.Ship;

import javax.annotation.Nullable;

@Mixin(BlockHitResult.class)
public abstract class BlockHitResultMixin implements BlockHitResultWithShip {
    @Unique
    private Ship tacztweaks$ship = null;

    @Override
    public @Nullable Ship tacztweaks$getShip() {
        return tacztweaks$ship;
    }

    @Override
    public void tacztweaks$setShip(Ship ship) {
        tacztweaks$ship = ship;
    }
}
