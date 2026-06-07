package me.muksc.tacztweaks.mixin.feature.gameplay.behaviour.tilt_rework;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.mixininterface.feature.synced_slide.SlideDataHolder;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = InaccuracyType.class, remap = false)
public abstract class InaccuracyTypeMixin {
    @Definition(id = "CROUCHING", field = "Lnet/minecraft/world/entity/Pose;CROUCHING:Lnet/minecraft/world/entity/Pose;", remap = true)
    @Expression("? == CROUCHING")
    @ModifyExpressionValue(method = "getInaccuracyType", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean tacztweaks$getInaccuracyType$tiltRework(
        boolean original,
        @Local(argsOnly = true) LivingEntity livingEntity
    ) {
        if (!Config.Gameplay.Behaviour.tiltRework()) return original;
        return original || SlideDataHolder.of(livingEntity).tacztweaks$getShouldSlide();
    }
}