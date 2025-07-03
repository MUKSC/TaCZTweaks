package me.muksc.tacztweaks.mixin.gun.unload;

import com.tacz.guns.entity.shooter.ShooterDataHolder;
import me.muksc.tacztweaks.mixininterface.gun.unload.ShooterDataHolderProvider;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LivingEntity.class, priority = 1500, remap = false)
public abstract class LivingEntityMixinMixin implements ShooterDataHolderProvider {
    @SuppressWarnings("MissingUnique")
    private ShooterDataHolder tacz$data;

    @Override
    public ShooterDataHolder tacztweaks$getShooterDataHolder() {
        return tacz$data;
    }
}
