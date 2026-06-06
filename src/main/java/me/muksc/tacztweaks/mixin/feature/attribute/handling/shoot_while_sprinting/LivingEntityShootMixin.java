package me.muksc.tacztweaks.mixin.feature.attribute.handling.shoot_while_sprinting;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.entity.shooter.LivingEntityShoot;
import me.muksc.tacztweaks.core.extension.DeferredHolderExt;
import me.muksc.tacztweaks.registry.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LivingEntityShoot.class, remap = false)
public abstract class LivingEntityShootMixin {
    @Shadow @Final private LivingEntity shooter;

    @Definition(id = "sprintTimeS", field = "Lcom/tacz/guns/entity/shooter/ShooterDataHolder;sprintTimeS:F")
    @Expression("?.sprintTimeS > 0.0")
    @ModifyExpressionValue(method = "shoot(Ljava/util/function/Supplier;Ljava/util/function/Supplier;JFZ)Lcom/tacz/guns/api/entity/ShootResult;", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$shoot$attribute$handling$shootWhileSprinting(boolean original) {
        double value = shooter.getAttributeValue(DeferredHolderExt.valueOrDelegate(ModAttributes.SHOOT_WHILE_SPRINTING));
        return original && value <= 0.0;
    }
}