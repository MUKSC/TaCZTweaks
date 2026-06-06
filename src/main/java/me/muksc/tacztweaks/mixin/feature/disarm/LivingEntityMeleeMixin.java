package me.muksc.tacztweaks.mixin.feature.disarm;

import com.tacz.guns.entity.shooter.LivingEntityMelee;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityMelee.class, remap = false)
public abstract class LivingEntityMeleeMixin {
    @Shadow @Final private LivingEntity shooter;

    @Inject(method = "melee", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$melee$disarm(CallbackInfo ci) {
        if (DisarmManager.shouldDisarm(shooter)) ci.cancel();
    }

    @Inject(method = "scheduleTickMelee", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$scheduleTickMelee$disarm(CallbackInfo ci) {
        if (DisarmManager.shouldDisarm(shooter)) ci.cancel();
    }
}