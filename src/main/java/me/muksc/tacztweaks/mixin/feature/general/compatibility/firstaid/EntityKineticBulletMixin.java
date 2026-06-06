package me.muksc.tacztweaks.mixin.feature.general.compatibility.firstaid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.TacHitResult;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.feature.general.compatibility.FirstAidManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if forge {
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
//?} else if neoforge || fabric {
/*import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
*///?}

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin {
    //? if forge {
    @WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"))
    private boolean tacztweaks$onHitEntity$firstAidCompat(
        IEventBus instance, Event event, Operation<Boolean> original,
        @Local(argsOnly = true) TacHitResult result
    ) {
        boolean cancelled = original.call(instance, event);
        if (Config.General.Compatibility.firstAidCompat() && !cancelled) {
            FirstAidManager.onHitEntity(EntityKineticBullet.class.cast(this), result);
        }
        return cancelled;
    }
    //?} else if neoforge {
    /*@WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/event/common/EntityHurtByGunEvent$Pre;isCanceled()Z"))
    private boolean tacztweaks$onHitEntity$firstAidCompat(
        EntityHurtByGunEvent.Pre instance, Operation<Boolean> original,
        @Local(argsOnly = true) TacHitResult result
    ) {
        boolean cancelled = original.call(instance);
        if (Config.General.Compatibility.firstAidCompat() && !cancelled) {
            FirstAidManager.onHitEntity(EntityKineticBullet.class.cast(this), result);
        }
        return cancelled;
    }
    *///?} else if fabric {
    /*@WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/event/common/EntityHurtByGunEvent$PreCallBack;post(Lcom/tacz/guns/api/event/common/EntityHurtByGunEvent$Pre;)V"))
    private void tacztweaks$onHitEntity$firstAidCompat(
        EntityHurtByGunEvent.PreCallBack instance, EntityHurtByGunEvent.Pre event, Operation<Void> original,
        @Local(argsOnly = true) TacHitResult result
    ) {
        original.call(instance, event);
        if (Config.General.Compatibility.firstAidCompat() && !event.isCanceled()) {
            FirstAidManager.onHitEntity(EntityKineticBullet.class.cast(this), result);
        }
    }
    *///?}
}