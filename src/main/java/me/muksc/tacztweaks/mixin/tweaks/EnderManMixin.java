package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.init.ModDamageTypes;
import me.muksc.tacztweaks.Config;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderMan.class)
public abstract class EnderManMixin {
    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean tacztweaks$hurt$bulletsAreProjectiles(DamageSource instance, TagKey<DamageType> pDamageTypeKey, Operation<Boolean> original) {
        boolean result = original.call(instance, pDamageTypeKey);
        if (!Config.Tweaks.INSTANCE.endermenEvadeBullets()) return result;
        return result || instance.is(ModDamageTypes.BULLETS_TAG);
    }
}
