package me.muksc.tacztweaks.mixin.tweaks;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.entity.shooter.LivingEntityAmmoCheck;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.core.Context;
import me.muksc.tacztweaks.registry.ModStatusEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    private void tacztweaks$consumesAmmoOrNot$disableAmmoConsumption(CallbackInfoReturnable<Boolean> cir) {
        if (!(shooter instanceof Player player) || !(player.getMainHandItem().getItem() instanceof IGun)) return;

        if (player.hasEffect(ModStatusEffects.ENDLESS_AMMO.get())) {
            cir.setReturnValue(false);
        } else if (Config.Tweaks.INSTANCE.infiniteAmmoDisablesConsumption() && Context.INSTANCE.hasInfiniteAmmo(player.getInventory(), player.getMainHandItem())) {
            cir.setReturnValue(false);
        }
    }
}
