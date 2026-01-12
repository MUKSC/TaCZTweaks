package me.muksc.tacztweaks.mixin.compat.firstaid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.init.ModDamageTypes;
import ichttt.mods.firstaid.common.EventHandler;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EventHandler.class, remap = false)
public abstract class EventHandlerMixin {
    @WrapOperation(method = "onLivingHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z", remap = true))
    private static boolean tacztweaks$onLivingHurt$bulletsAreProjectile(DamageSource instance, TagKey<DamageType> pDamageTypeKey, Operation<Boolean> original) {
        boolean result = original.call(instance, pDamageTypeKey);
        if (!Config.Compat.INSTANCE.firstAidCompat()) return result;
        return result || instance.is(ModDamageTypes.BULLETS_TAG);
    }
}
