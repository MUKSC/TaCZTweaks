package me.muksc.tacztweaks.mixin.compat.firstaid;

import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.TacHitResult;
import ichttt.mods.firstaid.common.EventHandler;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin {
    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z", ordinal = 0))
    private void tacztweaks$onHitEntity$fireProjectileImpactEvent(TacHitResult result, Vec3 startVec, Vec3 endVec, CallbackInfo ci) {
        if (!Config.Compat.INSTANCE.firstAidCompat()) return;
        var instance = EntityKineticBullet.class.cast(this);
        EventHandler.onProjectileImpact(new ProjectileImpactEvent(instance, result));
    }
}
