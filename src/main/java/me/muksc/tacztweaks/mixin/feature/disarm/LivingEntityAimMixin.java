package me.muksc.tacztweaks.mixin.feature.disarm;

import com.tacz.guns.entity.shooter.LivingEntityAim;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityAim.class, remap = false)
public abstract class LivingEntityAimMixin {
    @Shadow @Final private LivingEntity shooter;

    @Inject(method = "aim", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$aim$disarm(boolean isAim, CallbackInfo ci) {
        if (DisarmManager.shouldDisarm(shooter)) ci.cancel();
    }

    @Inject(method = "zoom", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$zoom$disarm(CallbackInfo ci) {
        if (DisarmManager.shouldDisarm(shooter)) ci.cancel();
    }
}