package me.muksc.tacztweaks.mixin.feature.general.compatibility.sable;

//? if 1.21.1 {
/*import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tacz.guns.client.particle.BulletHoleParticle;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
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

@Mixin(value = BulletHoleParticle.class, remap = false)
public abstract class BulletHoleParticleMixin extends TextureSheetParticle {
    protected BulletHoleParticleMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Shadow(remap = false) protected abstract boolean shouldRemove();

    @Unique
    private ClientSubLevel tacztweaks$subLevel = null;

    @Unique
    private Vector3d tacztweaks$subLevelPos = null;

    @ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/particle/BulletHoleParticle;shouldRemove()Z", remap = false))
    private boolean tacztweaks$init$sableCollisionCompat$deferRemoveCheck(boolean original) {
        return original && !Config.General.Compatibility.sableCompat();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void tacztweaks$init$sableCompat(ClientLevel world, double x, double y, double z, Direction direction, BlockPos pos, String ammoId, String gunId, String gunDisplayId, CallbackInfo ci) {
        if (!Config.General.Compatibility.sableCompat()) return;
        tacztweaks$subLevel = Sable.HELPER.getContainingClient(pos);
        if (tacztweaks$subLevel != null) tacztweaks$subLevelPos = new Vector3d(x, y, z);
        if (shouldRemove()) remove();
    }

    @Definition(id = "move", method = "Lnet/minecraft/world/phys/AABB;move(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;")
    @Definition(id = "pos", field = "Lcom/tacz/guns/client/particle/BulletHoleParticle;pos:Lnet/minecraft/core/BlockPos;")
    @Expression("?.move(this.pos)")
    @WrapOperation(method = "shouldRemove", at = @At("MIXINEXTRAS:EXPRESSION"), remap = false)
    private AABB tacztweaks$shouldRemove$sableCompat$fixMoveCoordinates(AABB instance, BlockPos pos, Operation<AABB> original) {
        if (!Config.General.Compatibility.sableCompat() || tacztweaks$subLevel == null) return original.call(instance, pos);
        return original.call(instance, BlockPos.containing(x, y, z));
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void tacztweaks$render$sableCompat$storeWorldPos(
        VertexConsumer buffer, Camera renderInfo, float partialTicks, CallbackInfo ci,
        @Share("worldPos") LocalRef<Vector3d> worldPosRef
    ) {
        if (!Config.General.Compatibility.sableCompat() || tacztweaks$subLevel == null) return;
        worldPosRef.set(tacztweaks$subLevel.renderPose().transformPosition(tacztweaks$subLevelPos, new Vector3d()));
    }

    @Definition(id = "lerp", method = "Lnet/minecraft/util/Mth;lerp(DDD)D")
    @Definition(id = "x", field = "Lcom/tacz/guns/client/particle/BulletHoleParticle;x:D")
    @Expression("lerp(?, ?, this.x)")
    @WrapOperation(method = "render", at = @At("MIXINEXTRAS:EXPRESSION"))
    private double tacztweaks$render$sableCompat$overrideX(
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
    private double tacztweaks$render$sableCompat$overrideY(
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
    private double tacztweaks$render$sableCompat$overrideZ(
        double delta, double start, double end, Operation<Double> original,
        @Share("worldPos") LocalRef<Vector3d> worldPosRef
    ) {
        Vector3d worldPos = worldPosRef.get();
        if (worldPos == null) return original.call(delta, start, end);
        return worldPos.z;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Direction;getRotation()Lorg/joml/Quaternionf;"))
    private Quaternionf tacztweaks$render$sableCompat$rotate(Quaternionf original) {
        if (!Config.General.Compatibility.sableCompat() || tacztweaks$subLevel == null) return original;
        Quaternionf quaternion = tacztweaks$subLevel.renderPose().orientation().get(new Quaternionf());
        return quaternion.mul(original);
    }
}
*///?} else {
import com.tacz.guns.client.particle.BulletHoleParticle;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BulletHoleParticle.class)
public abstract class BulletHoleParticleMixin { }
//?}