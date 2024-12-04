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

@Mixin(LivingEntityShoot.class)
public abstract class LivingEntityShootMixin {
    @Shadow(remap = false) @Final private LivingEntity shooter;

    @ModifyExpressionValue(method = "shoot", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/tacz/guns/entity/shooter/ShooterDataHolder;sprintTimeS:F", remap = false), remap = false)
    private float shootWhileSprinting(float original) {
        if (Config.shootWhileSprinting == Config.EShootWhileSprinting.DISABLED) return original;
        return 0.0F;
    }

    @Inject(method = "shoot", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/network/NetworkHandler;sendToTrackingEntity(Ljava/lang/Object;Lnet/minecraft/world/entity/Entity;)V", remap = false), remap = false)
    private void stopSprintingOnShot(Supplier<Float> pitch, Supplier<Float> yaw, CallbackInfoReturnable<ShootResult> cir) {
        if (Config.shootWhileSprinting != Config.EShootWhileSprinting.STOP_SPRINTING) return;
        shooter.setSprinting(false);
    }
}
