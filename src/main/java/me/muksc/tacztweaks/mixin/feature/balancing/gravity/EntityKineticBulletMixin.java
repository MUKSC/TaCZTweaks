package me.muksc.tacztweaks.mixin.feature.balancing.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.entity.EntityKineticBullet;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin {
    @ModifyExpressionValue(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/BulletData;getGravity()F"))
    private float tacztweaks$init$gravityModifier(float original) {
        return (float) Config.Balancing.Gravity.eval(original);
    }
}