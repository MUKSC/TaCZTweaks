package me.muksc.tacztweaks.mixin.compat.lso;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.entity.EntityKineticBullet;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.EntityKineticBulletExtension;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sfiomn.legendarysurvivaloverhaul.api.bodydamage.BodyPartEnum;
import sfiomn.legendarysurvivaloverhaul.common.events.CommonForgeEvents;

import java.util.List;

@Mixin(value = CommonForgeEvents.class, remap = false)
public abstract class CommonForgeEventsMixin {
    @ModifyExpressionValue(method = "onEntityHurtDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z", remap = true))
    private static boolean onEntityHurtDamage$treatBulletsAsProjectile(boolean original, @Local DamageSource source) {
        if (!Config.Compat.INSTANCE.lsoCompat()) return original;
        return original || source.getDirectEntity() instanceof EntityKineticBullet;
    }

    @WrapOperation(method = "onEntityHurtDamage", at = @At(value = "INVOKE", target = "Lsfiomn/legendarysurvivaloverhaul/util/PlayerModelUtil;getPreciseEntityImpact(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/player/Player;)Ljava/util/List;"))
    private static List<BodyPartEnum> onEntityHurtDamage$correctPosition(Entity hitEntity, Player player, Operation<List<BodyPartEnum>> original) {
        if (!Config.Compat.INSTANCE.lsoCompat()) return original.call(hitEntity, player);
        if (!(hitEntity instanceof EntityKineticBullet bullet)) return original.call(hitEntity, player);
        Vec3 originalPosition = bullet.position();
        try {
            EntityKineticBulletExtension ext = (EntityKineticBulletExtension) bullet;
            bullet.setPos(ext.tacztweaks$getPosition());
            return original.call(hitEntity, player);
        } finally {
            bullet.setPos(originalPosition);
        }
    }
}
