package me.muksc.tacztweaks.mixin.gun.movement;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.gameplay.LocalPlayerShoot;
import me.muksc.tacztweaks.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerShoot.class, remap = false)
public abstract class LocalPlayerShootMixin {
    @ModifyExpressionValue(method = "shoot", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/entity/IGunOperator;getSynSprintTime()F"))
    private float tacztweaks$shoot$shootWhileSprinting(float original) {
        if (!Config.Gun.INSTANCE.shootWhileSprinting()) return original;
        return 0.0F;
    }
}
