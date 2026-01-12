package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.util.TacHitResult;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin {
    @Definition(id = "DAMAGE_BASE_MULTIPLIER", field = "Lcom/tacz/guns/config/sync/SyncConfig;DAMAGE_BASE_MULTIPLIER:Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;")
    @Definition(id = "get", method = "Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;get()Ljava/lang/Object;")
    @Definition(id = "Double", type = Double.class)
    @Expression("? * (Double) DAMAGE_BASE_MULTIPLIER.get()")
    @ModifyExpressionValue(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double tacztweaks$init$damageModifier(double original) {
        return AttachmentPropertyManager.eval(Config.Modifiers.Damage.INSTANCE.toTaCZ(), original);
    }

    @ModifyExpressionValue(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/entity/EntityKineticBullet;getDamage(Lnet/minecraft/world/phys/Vec3;)F", ordinal = 1))
    private float tacztweaks$onHitEntity$playerDamageModifier(float original, @Local(argsOnly = true) TacHitResult result) {
        if (!(result.getEntity() instanceof Player)) return original;
        return (float) AttachmentPropertyManager.eval(Config.Modifiers.PlayerDamage.INSTANCE.toTaCZ(), original);
    }

    @ModifyExpressionValue(method = "onHitEntity", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/tacz/guns/entity/EntityKineticBullet;headShot:F"))
    private float tacztweaks$onHitEntity$playerHeadshotModifier(float original, @Local(argsOnly = true) TacHitResult result) {
        if (!(result.getEntity() instanceof Player)) return original;
        return (float) AttachmentPropertyManager.eval(Config.Modifiers.PlayerHeadshot.INSTANCE.toTaCZ(), original);
    }

    @ModifyExpressionValue(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/BulletData;getGravity()F"))
    private float tacztweaks$init$gravityModifier(float original) {
        return (float) AttachmentPropertyManager.eval(Config.Modifiers.Gravity.INSTANCE.toTaCZ(), original);
    }

    @ModifyExpressionValue(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/BulletData;getFriction()F"))
    private float tacztweaks$init$frictionModifier(float original) {
        return (float) AttachmentPropertyManager.eval(Config.Modifiers.Friction.INSTANCE.toTaCZ(), original);
    }
}
