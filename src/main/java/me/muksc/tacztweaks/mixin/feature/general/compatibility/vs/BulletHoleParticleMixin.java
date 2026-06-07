package me.muksc.tacztweaks.mixin.feature.general.compatibility.vs;

//? if 1.20.1 {
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tacz.guns.client.particle.BulletHoleParticle;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(BulletHoleParticle.class)
public abstract class BulletHoleParticleMixin extends TextureSheetParticle {
    protected BulletHoleParticleMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Shadow(remap = false) protected abstract boolean shouldRemove();

    @Unique
    private ClientShip tacztweaks$ship = null;

    @Unique
    private Vector3d tacztweaks$shipPos = null;

    @ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/particle/BulletHoleParticle;shouldRemove()Z", remap = false))
    private boolean tacztweaks$init$vsCompat$deferRemoveCheck(boolean original) {
        return original && !Config.General.Compatibility.vsCompat();
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "<init>", at = @At("TAIL"))
    private void tacztweaks$init$vsCompat(ClientLevel world, double x, double y, double z, Direction direction, BlockPos pos, String ammoId, String gunId, String gunDisplayId, CallbackInfo ci) {
        if (!Config.General.Compatibility.vsCompat()) return;
        tacztweaks$ship = VSGameUtilsKt.getShipObjectManagingPos(world, pos);
        if (tacztweaks$ship != null) tacztweaks$shipPos = tacztweaks$ship.getTransform().getWorldToShip().transformPosition(x, y, z, new Vector3d());
        if (shouldRemove()) remove();
    }

    @Definition(id = "move", method = "Lnet/minecraft/world/phys/AABB;move(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;", remap = true)
    @Definition(id = "pos", field = "Lcom/tacz/guns/client/particle/BulletHoleParticle;pos:Lnet/minecraft/core/BlockPos;")
    @Expression("?.move(this.pos)")
    @WrapOperation(method = "shouldRemove", at = @At("MIXINEXTRAS:EXPRESSION"), remap = false)
    private AABB tacztweaks$shouldRemove$vsCompat$fixMoveCoordinates(AABB instance, BlockPos pos, Operation<AABB> original) {
        if (!Config.General.Compatibility.vsCompat() || tacztweaks$ship == null) return original.call(instance, pos);
        return original.call(instance, BlockPos.containing(x, y, z));
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void tacztweaks$render$vsCompat$storeWorldPos(
        VertexConsumer buffer, Camera renderInfo, float partialTicks, CallbackInfo ci,
        @Share("worldPos") LocalRef<Vector3d> worldPosRef
    ) {
        if (!Config.General.Compatibility.vsCompat() || tacztweaks$ship == null) return;
        worldPosRef.set(tacztweaks$ship.getRenderTransform().getShipToWorld().transformPosition(tacztweaks$shipPos, new Vector3d()));
    }

    @Definition(id = "lerp", method = "Lnet/minecraft/util/Mth;lerp(DDD)D")
    @Definition(id = "x", field = "Lcom/tacz/guns/client/particle/BulletHoleParticle;x:D")
    @Expression("lerp(?, ?, this.x)")
    @WrapOperation(method = "render", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double tacztweaks$render$vsCompat$overrideX(
        double delta, double start, double end, Operation<Double> original,
        @Share("worldPos") LocalRef<Vector3d> worldPosRef
    ) {
        Vector3d worldPos = worldPosRef.get();
        if (worldPos == null) return original.call(delta, start, end);
        return worldPos.x;
    }

    @Definition(id = "lerp", method = "Lnet/minecraft/util/Mth;lerp(DDD)D")
    @Definition(id = "y", field = "Lcom/tacz/guns/client/particle/BulletHoleParticle;y:D")
    @Expression("lerp(?, ?, this.y)")
    @WrapOperation(method = "render", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double tacztweaks$render$vsCompat$overrideY(
        double delta, double start, double end, Operation<Double> original,
        @Share("worldPos") LocalRef<Vector3d> worldPosRef
    ) {
        Vector3d worldPos = worldPosRef.get();
        if (worldPos == null) return original.call(delta, start, end);
        return worldPos.y;
    }

    @Definition(id = "lerp", method = "Lnet/minecraft/util/Mth;lerp(DDD)D")
    @Definition(id = "z", field = "Lcom/tacz/guns/client/particle/BulletHoleParticle;z:D")
    @Expression("lerp(?, ?, this.z)")
    @WrapOperation(method = "render", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double tacztweaks$render$vsCompat$overrideZ(
        double delta, double start, double end, Operation<Double> original,
        @Share("worldPos") LocalRef<Vector3d> worldPosRef
    ) {
        Vector3d worldPos = worldPosRef.get();
        if (worldPos == null) return original.call(delta, start, end);
        return worldPos.z;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Direction;getRotation()Lorg/joml/Quaternionf;"))
    private Quaternionf tacztweaks$render$vsCompat$rotate(Quaternionf original) {
        if (!Config.General.Compatibility.vsCompat() || tacztweaks$ship == null) return original;
        Quaternionf quaternion = tacztweaks$ship.getRenderTransform().getShipToWorldRotation().get(new Quaternionf());
        return quaternion.mul(original);
    }
}
//?} else {
/*import com.tacz.guns.client.particle.BulletHoleParticle;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BulletHoleParticle.class)
public abstract class BulletHoleParticleMixin { }
*///?}