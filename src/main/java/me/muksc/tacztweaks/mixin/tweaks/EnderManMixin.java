package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.init.ModDamageTypes;
import me.muksc.tacztweaks.Config;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderMan.class)
public abstract class EnderManMixin {
    @ModifyExpressionValue(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean tacztweaks$hurt$bulletsAreProjectiles(boolean original, @Local(argsOnly = true, ordinal = 0) DamageSource pSource) {
        if (!Config.Tweaks.INSTANCE.endermenEvadeBullets()) return original;
        return original || pSource.is(ModDamageTypes.BULLETS_TAG);
    }
}
