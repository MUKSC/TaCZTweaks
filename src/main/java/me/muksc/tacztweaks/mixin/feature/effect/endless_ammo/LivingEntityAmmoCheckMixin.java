package me.muksc.tacztweaks.mixin.feature.effect.endless_ammo;

import com.tacz.guns.entity.shooter.LivingEntityAmmoCheck;
import me.muksc.tacztweaks.core.extension.DeferredHolderExt;
import me.muksc.tacztweaks.registry.ModStatusEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityAmmoCheck.class, remap = false)
public abstract class LivingEntityAmmoCheckMixin {
    @Shadow @Final private LivingEntity shooter;

    @Inject(method = "consumesAmmoOrNot", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$consumesAmmoOrNot$endlessAmmo(CallbackInfoReturnable<Boolean> cir) {
        if (shooter.hasEffect(DeferredHolderExt.valueOrDelegate(ModStatusEffects.ENDLESS_AMMO))) cir.setReturnValue(false);
    }
}