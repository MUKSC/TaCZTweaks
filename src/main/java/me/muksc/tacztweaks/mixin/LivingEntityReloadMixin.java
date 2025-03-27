package me.muksc.tacztweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.entity.shooter.LivingEntityReload;
import me.muksc.tacztweaks.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LivingEntityReload.class, remap = false)
public abstract class LivingEntityReloadMixin {
    @ModifyExpressionValue(method = "lambda$reload$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/entity/shooter/LivingEntityShoot;getShootCoolDown()J"))
    private long tacztweaks$reload$allowReloadWhileShoot(long original) {
        if (!Config.Gun.INSTANCE.reloadWhileShooting()) return original;
        return 0L;
    }
}
