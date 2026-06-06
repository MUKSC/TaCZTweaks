package me.muksc.tacztweaks.mixin.feature.disarm;

import com.tacz.guns.entity.shooter.LivingEntityBolt;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityBolt.class, remap = false)
public abstract class LivingEntityBoltMixin {
    @Shadow @Final private LivingEntity shooter;

    @Inject(method = "bolt", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$bolt$disarm(CallbackInfo ci) {
        if (DisarmManager.shouldDisarm(shooter)) ci.cancel();
    }

    @Inject(method = "tickBolt", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$tickBolt$disarm(CallbackInfo ci) {
        if (DisarmManager.shouldDisarm(shooter)) ci.cancel();
    }
}