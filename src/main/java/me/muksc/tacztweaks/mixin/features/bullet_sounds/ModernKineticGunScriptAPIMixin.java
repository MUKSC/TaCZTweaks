package me.muksc.tacztweaks.mixin.features.bullet_sounds;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import me.muksc.tacztweaks.data.BulletSoundsManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public abstract class ModernKineticGunScriptAPIMixin {
    @WrapOperation(method = "lambda$shootOnce$2", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/gun/AbstractGunItem;doBulletSpread(Lcom/tacz/guns/entity/shooter/ShooterDataHolder;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/projectile/Projectile;IFFFF)V"))
    private void tacztweaks$shootOnce$handleAirspace(AbstractGunItem instance, ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter, Projectile projectile, int bulletCnt, float processedSpeed, float inaccuracy, float pitch, float yaw, Operation<Void> original) {
        original.call(instance, dataHolder, gunItem, shooter, projectile, bulletCnt, processedSpeed, inaccuracy, pitch, yaw);
        if (!(shooter.level() instanceof ServerLevel level)) return;
        if (!(projectile instanceof EntityKineticBullet bullet)) return;
        if (bulletCnt != 0) return;
        BulletSoundsManager.INSTANCE.handleAirspace(level, bullet);
    }
}
