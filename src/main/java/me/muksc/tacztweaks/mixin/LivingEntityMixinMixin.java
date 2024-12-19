package me.muksc.tacztweaks.mixin;

import com.tacz.guns.entity.shooter.ShooterDataHolder;
import me.muksc.tacztweaks.ShooterDataHolderProvider;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.*;

@Debug(export = true)
@Mixin(value = LivingEntity.class, priority = 1500, remap = false)
public abstract class LivingEntityMixinMixin implements ShooterDataHolderProvider {
    @SuppressWarnings("MissingUnique")
    @Final private ShooterDataHolder tacz$data;

    @Override
    public ShooterDataHolder tacztweaks$getShooterDataHolder() {
        return tacz$data;
    }
}
