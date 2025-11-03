package me.muksc.tacztweaks.mixin.features;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import me.muksc.tacztweaks.mixininterface.features.EntityKineticBulletExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public abstract class ModernKineticGunScriptAPIMixin {
    @Unique
    private boolean tacztweaks$firstOfBurst = false;

    @Unique
    private boolean tacztweaks$firstOfPellets = false;

    @Inject(method = "shootOnce", at = @At("HEAD"))
    private void tacztweaks$shootOnce$resetBurst(boolean consumeAmmo, CallbackInfo ci) {
        tacztweaks$firstOfBurst = true;
    }

    @Inject(method = "lambda$shootOnce$2", at = @At("HEAD"))
    private void tacztweaks$shootOnce$resetPellets(boolean consumeAmmo, GunData gunData, int bulletAmount, BulletData bulletData, IGunOperator gunOperator, float processedSpeed, float inaccuracy, int soundDistance, boolean useSilenceSound, CallbackInfoReturnable<Boolean> cir) {
        tacztweaks$firstOfPellets = true;
    }

    @Definition(id = "EntityKineticBullet", type = EntityKineticBullet.class)
    @Expression("new EntityKineticBullet(?, ?, ?, ?, ?, ?, ?, ?, ?)")
    @ModifyExpressionValue(method = "lambda$shootOnce$2", at = @At("MIXINEXTRAS:EXPRESSION"))
    private EntityKineticBullet tacztweaks$shootOnce$apply(EntityKineticBullet original) {
        var ext = (EntityKineticBulletExtension) original;
        if (tacztweaks$firstOfBurst) ext.tacztweaks$markFirstOfBurst();
        if (tacztweaks$firstOfPellets) ext.tacztweaks$markFirstOfPellets();
        tacztweaks$firstOfBurst = false;
        tacztweaks$firstOfPellets = false;
        return original;
    }
}
