package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.mixininterface.gun.SlideDataHolder;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = InaccuracyType.class, remap = false)
public abstract class InaccuracyTypeMixin {
    @Definition(id = "CROUCHING", field = "Lnet/minecraft/world/entity/Pose;CROUCHING:Lnet/minecraft/world/entity/Pose;")
    @Expression("? == CROUCHING")
    @ModifyExpressionValue(method = "getInaccuracyType", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean tacztweaks$getInaccuracyType$betterGunTilt(boolean original, @Local(argsOnly = true) LivingEntity livingEntity) {
        if (!Config.Tweaks.INSTANCE.betterGunTilt()) return original;
        return original || ((SlideDataHolder) livingEntity).tacztweaks$getShouldSlide();
    }
}
