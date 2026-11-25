package me.muksc.tacztweaks.mixin.feature.general.compatibility.sable;

//? if 1.21.1 {
/*import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tacz.guns.entity.EntityKineticBullet;
import dev.ryanhcode.sable.Sable;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin {
    @WrapMethod(method = "getDamage")
    private float tacztweaks$getDamage$sableCompat(Vec3 hitVec, Operation<Float> original) {
        if (!Config.General.Compatibility.sableCompat()) return original.call(hitVec);
        EntityKineticBullet instance = EntityKineticBullet.class.cast(this);
        return original.call(Sable.HELPER.projectOutOfSubLevel(instance.level(), hitVec));
    }
}
*///?} else {
import com.tacz.guns.entity.EntityKineticBullet;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityKineticBullet.class)
public abstract class EntityKineticBulletMixin { }
//?}