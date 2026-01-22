package me.muksc.tacztweaks.mixin.compat.firstaid;

import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.TacHitResult;
import me.muksc.tacztweaks.mixininterface.features.EntityKineticBulletExtension;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Captures the exact hit location when a TacZ bullet hits an entity.
 * This data is used by FirstAid integration for precise bodypart targeting.
 */
@Mixin(value = EntityKineticBullet.class, remap = false)
public class EntityKineticBulletHitCaptureMixin {

    @Inject(
        method = "onHitEntity",
        at = @At("HEAD")
    )
    private void tacztweaks$captureHitLocation(TacHitResult result, Vec3 startVec, Vec3 endVec, CallbackInfo ci) {
        // Store the exact hit location in the bullet for FirstAid to use
        Vec3 hitLocation = result.getLocation();
        ((EntityKineticBulletExtension) this).tacztweaks$setLastHitLocation(hitLocation);
    }
}
