package me.muksc.tacztweaks.mixin.compat.firstaid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.init.ModDamageTypes;
import ichttt.mods.firstaid.common.EventHandler;
import me.muksc.tacztweaks.Config;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EventHandler.class, remap = false)
public abstract class EventHandlerMixin {
    @ModifyExpressionValue(method = "onLivingHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z", remap = true))
    private static boolean bulletsAreProjectile(boolean original, @Local DamageSource source) {
        if (!Config.firstAidCompat) return original;
        return original || source.is(ModDamageTypes.BULLETS_TAG);
    }
}
