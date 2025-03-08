package me.muksc.tacztweaks.mixin.client;

import com.tacz.guns.client.renderer.entity.EntityBulletRenderer;
import com.tacz.guns.entity.EntityKineticBullet;
import me.muksc.tacztweaks.Config;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityBulletRenderer.class, remap = false)
public abstract class EntityBulletRendererMixin {
    @Inject(method = "shouldRender(Lcom/tacz/guns/entity/EntityKineticBullet;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z", at = @At("HEAD"), cancellable = true)
    private void alwaysRender(EntityKineticBullet bullet, Frustum camera, double pCamX, double pCamY, double pCamZ, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.Gun.INSTANCE.disableBulletCulling()) return;
        cir.setReturnValue(true);
    }
}
