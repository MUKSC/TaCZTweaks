package me.muksc.tacztweaks.mixin.feature.gameplay.behaviour.endermen_evade_bullets;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.init.ModDamageTypes;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderMan.class)
public abstract class EnderManMixin {
    @Definition(id = "is", method = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    @Definition(id = "IS_PROJECTILE", field = "Lnet/minecraft/tags/DamageTypeTags;IS_PROJECTILE:Lnet/minecraft/tags/TagKey;")
    @Expression("?.is(IS_PROJECTILE)")
    @WrapOperation(method = "hurt", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$hurt$endermenEvadeBullets$markAsProjectile(DamageSource instance, TagKey<DamageType> damageTypeKey, Operation<Boolean> original) {
        return original.call(instance, damageTypeKey)
            || (Config.Gameplay.Behaviour.endermenEvadeBullets() && instance.is(ModDamageTypes.BULLETS_TAG));
    }
}