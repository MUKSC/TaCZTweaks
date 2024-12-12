package me.muksc.tacztweaks.mixin.accessor;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.client.gameplay.LocalPlayerShoot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Predicate;

@Mixin(value = LocalPlayerShoot.class, remap = false)
public interface LocalPlayerShootAccessor {
    @Accessor("SHOOT_LOCKED_CONDITION")
    static Predicate<IGunOperator> getShootLockedCondition() {
        throw new AssertionError();
    }
}
