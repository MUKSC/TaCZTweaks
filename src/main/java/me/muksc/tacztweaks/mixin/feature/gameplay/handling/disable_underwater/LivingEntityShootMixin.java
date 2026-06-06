package me.muksc.tacztweaks.mixin.feature.gameplay.handling.disable_underwater;

import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.entity.shooter.LivingEntityShoot;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.entity.LivingEntity;
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

    //~ if neoforge || fabric 'Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z' -> 'Lcom/tacz/guns/api/event/common/GunShootEvent;isCanceled()Z'
    @Inject(method = "shoot(Ljava/util/function/Supplier;Ljava/util/function/Supplier;JFZ)Lcom/tacz/guns/api/entity/ShootResult;", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"), cancellable = true)
    private void tacztweaks$shoot$disableUnderwater(Supplier<Float> pitch, Supplier<Float> yaw, long timestamp, float chargeProgress, boolean hasChargeContext, CallbackInfoReturnable<ShootResult> cir) {
        if (!Config.Gameplay.Handling.disableUnderwater()) return;
        if (shooter.isUnderWater()) cir.setReturnValue(ShootResult.FORGE_EVENT_CANCEL);
    }
}