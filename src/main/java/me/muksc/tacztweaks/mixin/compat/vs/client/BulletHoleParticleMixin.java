package me.muksc.tacztweaks.mixin.compat.vs.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tacz.guns.client.particle.BulletHoleParticle;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.compat.vs.ParticleExtension;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ClientShip;

@Mixin(BulletHoleParticle.class)
public abstract class BulletHoleParticleMixin extends TextureSheetParticle {
    protected BulletHoleParticleMixin(ClientLevel pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ);
    }

    @Unique
    private boolean tacztweaks$initialized = false;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void tacztweaks$init(ClientLevel world, double x, double y, double z, Direction direction, BlockPos pos, String ammoId, String gunId, String gunDisplayId, CallbackInfo ci) {
        tacztweaks$initialized = true;
    }

    @Inject(method = "shouldRemove", at = @At("HEAD"), cancellable = true, remap = false)
    private void tacztweaks$shouldRemove$surviveUntilInitialization(CallbackInfoReturnable<Boolean> cir) {
        if (tacztweaks$initialized) return;
        cir.setReturnValue(false);
    }

    @WrapOperation(method = "shouldRemove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;intersects(DDDDDD)Z", remap = true), remap = false)
    private boolean tacztweaks$shouldRemove$transformToShip(AABB instance, double pX1, double pY1, double pZ1, double pX2, double pY2, double pZ2, Operation<Boolean> original) {
        if (!Config.Compat.INSTANCE.vsCollisionCompat()) return original.call(instance, pX1, pY1, pZ1, pX2, pY2, pZ2);
        ParticleExtension ext = (ParticleExtension) this;
        Vector3d pos = ext.tacztweaks$getShipPos();
        if (pos == null) return original.call(instance, pX1, pY1, pZ1, pX2, pY2, pZ2);
        return original.call(instance, pos.x - 0.1, pos.y - 0.1, pos.z - 0.1, pos.x + 0.1, pos.y + 0.1, pos.z + 0.1);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/particle/BulletHoleParticle;shouldRemove()Z", remap = false))
    private void tacztweaks$tick$updatePosition(CallbackInfo ci) {
        if (!Config.Compat.INSTANCE.vsCollisionCompat()) return;
        ParticleExtension ext = (ParticleExtension) this;
        ClientShip ship = ext.tacztweaks$getShip();
        if (ship == null) return;
        Vector3d worldPos = ship.getRenderTransform().getShipToWorld().transformPosition(ext.tacztweaks$getShipPos(), new Vector3d());
        setPos(worldPos.x, worldPos.y, worldPos.z);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void tacztweaks$render$updatePosition(VertexConsumer buffer, Camera renderInfo, float partialTicks, CallbackInfo ci) {
        if (!Config.Compat.INSTANCE.vsCollisionCompat()) return;
        ParticleExtension ext = (ParticleExtension) this;
        ClientShip ship = ext.tacztweaks$getShip();
        if (ship == null) return;
        Vector3d worldPos = ship.getRenderTransform().getShipToWorld().transformPosition(ext.tacztweaks$getShipPos(), new Vector3d());
        setPos(worldPos.x, worldPos.y, worldPos.z);
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(DDD)D", ordinal = 0))
    private double tacztweaks$render$modifyX(double original) {
        if (!Config.Compat.INSTANCE.vsCollisionCompat()) return original;
        return this.x;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(DDD)D", ordinal = 1))
    private double tacztweaks$render$modifyY(double original) {
        if (!Config.Compat.INSTANCE.vsCollisionCompat()) return original;
        return this.y;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(DDD)D", ordinal = 2))
    private double tacztweaks$render$modifyZ(double original) {
        if (!Config.Compat.INSTANCE.vsCollisionCompat()) return original;
        return this.z;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Direction;getRotation()Lorg/joml/Quaternionf;"))
    private Quaternionf tacztweaks$render$rotate(Quaternionf original) {
        if (!Config.Compat.INSTANCE.vsCollisionCompat()) return original;
        ParticleExtension ext = (ParticleExtension) this;
        ClientShip ship = ext.tacztweaks$getShip();
        if (ship == null) return original;
        Quaternionf quaternion = ship.getRenderTransform().getShipToWorldRotation().get(new Quaternionf());
        return quaternion.mul(original);
    }
}
