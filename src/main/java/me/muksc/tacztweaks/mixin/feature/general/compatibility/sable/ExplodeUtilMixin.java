package me.muksc.tacztweaks.mixin.feature.general.compatibility.sable;

//? if 1.21.1 {
/*import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.tacz.guns.util.ExplodeUtil;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ExplodeUtil.class, remap = false)
public abstract class ExplodeUtilMixin {
    @Inject(method = "createExplosion", at = @At("HEAD"))
    private static void tacztweaks$createExplosion$normalizePosition(
        Entity owner, Entity exploder, float damage, float radius, boolean knockback, boolean destroy, Vec3 hitPos, CallbackInfo ci,
        @Local(argsOnly = true) LocalRef<Vec3> hitPosRef
    ) {
        SubLevel subLevel = Sable.HELPER.getContaining(exploder.level(), hitPos);
        if (subLevel == null) return;
        hitPosRef.set(subLevel.logicalPose().transformPosition(hitPos));
    }
}
*///?} else {
import com.tacz.guns.util.ExplodeUtil;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ExplodeUtil.class, remap = false)
public abstract class ExplodeUtilMixin { }
//?}