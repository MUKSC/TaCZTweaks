package me.muksc.tacztweaks.mixin.feature.general.compatibility.sable.lrtactical;

//? if 1.21.1 && neoforge {
/*import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import me.muksc.tacztweaks.config.Config;
import me.xjqsh.lrtactical.entity.ThrowableItemEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ThrowableItemEntity.class, remap = false)
public abstract class ThrowableItemEntityMixin {
    @Definition(id = "start", local = @Local(type = Vec3.class, name = "start"))
    @Definition(id = "position", method = "Lme/xjqsh/lrtactical/entity/ThrowableItemEntity;position()Lnet/minecraft/world/phys/Vec3;")
    @Expression("start = @(this.position())")
    @ModifyExpressionValue(method = "doMultiBounce", at = @At("MIXINEXTRAS:EXPRESSION"))
    private Vec3 tacztweaks$doMultiBounce$normalizePosition(Vec3 original) {
        if (!Config.General.Compatibility.sableCompat()) return original;
        ThrowableItemEntity instance = ThrowableItemEntity.class.cast(this);
        SubLevel subLevel = Sable.HELPER.getContaining(instance.level(), original);
        if (subLevel == null) return  original;
        return subLevel.logicalPose().transformPosition(original);
    }

    @Definition(id = "hit", local = @Local(type = Vec3.class, name = "hit"))
    @Definition(id = "blockResult", local = @Local(type = BlockHitResult.class, name = "blockResult"))
    @Definition(id = "getLocation", method = "Lnet/minecraft/world/phys/BlockHitResult;getLocation()Lnet/minecraft/world/phys/Vec3;")
    @Expression("hit = @(blockResult.getLocation())")
    @ModifyExpressionValue(method = "doMultiBounce", at = @At("MIXINEXTRAS:EXPRESSION"))
    private Vec3 tacztweaks$doMultiBounce$normalizeBlockHitLocation(Vec3 original) {
        if (!Config.General.Compatibility.sableCompat()) return original;
        ThrowableItemEntity instance = ThrowableItemEntity.class.cast(this);
        SubLevel subLevel = Sable.HELPER.getContaining(instance.level(), original);
        if (subLevel == null) return  original;
        return subLevel.logicalPose().transformPosition(original);
    }

    @Definition(id = "hit", local = @Local(type = Vec3.class, name = "hit"))
    @Definition(id = "hitResult", local = @Local(type = HitResult.class, name = "hitResult"))
    @Definition(id = "getLocation", method = "Lnet/minecraft/world/phys/HitResult;getLocation()Lnet/minecraft/world/phys/Vec3;")
    @Expression("hit = @(hitResult.getLocation())")
    @ModifyExpressionValue(method = "doMultiBounce", at = @At("MIXINEXTRAS:EXPRESSION"))
    private Vec3 tacztweaks$doMultiBounce$normalizeEntityHitLocation(Vec3 original) {
        if (!Config.General.Compatibility.sableCompat()) return original;
        ThrowableItemEntity instance = ThrowableItemEntity.class.cast(this);
        SubLevel subLevel = Sable.HELPER.getContaining(instance.level(), original);
        if (subLevel == null) return  original;
        return subLevel.logicalPose().transformPosition(original);
    }
}
*///?} else {
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public abstract class ThrowableItemEntityMixin { }
//?}