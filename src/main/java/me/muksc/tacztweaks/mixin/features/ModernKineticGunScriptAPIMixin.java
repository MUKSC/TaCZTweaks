package me.muksc.tacztweaks.mixin.features;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import me.muksc.tacztweaks.mixininterface.features.EntityKineticBulletExtension;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public abstract class ModernKineticGunScriptAPIMixin {
    @Unique
    private int tacztweaks$burstIndex = 0;

    @Unique
    private int tacztweaks$pelletIndex = 0;

    @Inject(method = "shootOnce", at = @At("HEAD"))
    private void tacztweaks$shootOnce$onInit(boolean consumeAmmo, CallbackInfo ci) {
        tacztweaks$burstIndex = 0;
    }

    @WrapMethod(method = "lambda$shootOnce$2")
    private boolean tacztweaks$shootOnce$onInitBurst(boolean consumeAmmo, GunData gunData, int bulletAmount, BulletData bulletData, IGunOperator gunOperator, float processedSpeed, float inaccuracy, int soundDistance, boolean useSilenceSound, Operation<Boolean> original) {
        try {
            tacztweaks$pelletIndex = 0;
            return original.call(consumeAmmo, gunData, bulletAmount, bulletData, gunOperator, processedSpeed, inaccuracy, soundDistance, useSilenceSound);
        } finally {
            tacztweaks$burstIndex++;
        }
    }

    @ModifyArg(method = "lambda$shootOnce$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), index = 0)
    private Entity tacztweaks$shootOnce$apply(Entity pEntity) {
        var ext = (EntityKineticBulletExtension) pEntity;
        ext.tacztweaks$setBurstIndex(tacztweaks$burstIndex);
        ext.tacztweaks$setPelletIndex(tacztweaks$pelletIndex++);
        return pEntity;
    }
}
