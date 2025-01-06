package me.muksc.tacztweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.entity.shooter.LivingEntityShoot;
import me.muksc.tacztweaks.Config;
import net.minecraft.world.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(value = LivingEntityShoot.class, remap = false)
public abstract class LivingEntityShootMixin {
    @Shadow @Final private LivingEntity shooter;

    @ModifyExpressionValue(method = "shoot", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/tacz/guns/entity/shooter/ShooterDataHolder;sprintTimeS:F"))
    private float shootWhileSprinting(float original) {
        if (Config.shootWhileSprinting == Config.EShootWhileSprinting.DISABLED) return original;
        return 0.0F;
    }

    @Inject(method = "shoot", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/network/NetworkHandler;sendToTrackingEntity(Ljava/lang/Object;Lnet/minecraft/world/entity/Entity;)V"))
    private void stopSprintingOnShot(Supplier<Float> pitch, Supplier<Float> yaw, long timestamp, CallbackInfoReturnable<ShootResult> cir) {
        if (Config.shootWhileSprinting != Config.EShootWhileSprinting.STOP_SPRINTING) return;
        shooter.setSprinting(false);
    }
}
