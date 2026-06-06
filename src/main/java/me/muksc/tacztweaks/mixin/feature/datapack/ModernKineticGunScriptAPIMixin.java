package me.muksc.tacztweaks.mixin.feature.datapack;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import me.muksc.tacztweaks.mixininterface.feature.datapack.TaCZTweaksBullet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public abstract class ModernKineticGunScriptAPIMixin {
    @Unique
    private int tacztweaks$burstIndex = 0;

    @Inject(method = "shootOnce", at = @At("HEAD"))
    private void tacztweaks$shootOnce$onInit(boolean consumeAmmo, CallbackInfo ci) {
        tacztweaks$burstIndex = 0;
    }

    @WrapMethod(method = "lambda$shootOnce$2")
    private boolean tacztweaks$shootOnce$onInitBurst(
        boolean consumeAmmo, GunData gunData, int bulletAmount, BulletData bulletData, IGunOperator gunOperator, float shotDamageMultiplier, float processedSpeed, float inaccuracy, int soundDistance, boolean useSilenceSound, Operation<Boolean> original,
        @Share("pelletIndex") LocalIntRef pelletIndexRef
    ) {
        try {
            pelletIndexRef.set(0);
            return original.call(consumeAmmo, gunData, bulletAmount, bulletData, gunOperator, shotDamageMultiplier, processedSpeed, inaccuracy, soundDistance, useSilenceSound);
        } finally {
            tacztweaks$burstIndex++;
        }
    }

    @WrapOperation(method = "lambda$shootOnce$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", remap = true))
    private boolean tacztweaks$shootOnce$apply(
        Level instance, Entity entity, Operation<Boolean> original,
        @Share("pelletIndex") LocalIntRef pelletIndexRef
    ) {
        if (entity instanceof EntityKineticBullet bullet) {
            TaCZTweaksBullet ext = TaCZTweaksBullet.of(bullet);
            ext.tacztweaks$setBurstIndex(tacztweaks$burstIndex);
            int pelletIndex = pelletIndexRef.get();
            ext.tacztweaks$setPelletIndex(pelletIndex);
            pelletIndexRef.set(pelletIndex + 1);
        }
        return original.call(instance, entity);
    }
}