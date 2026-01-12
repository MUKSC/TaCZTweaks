package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.modifier.custom.AmmoSpeedModifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = AmmoSpeedModifier.class, remap = false)
public abstract class AmmoSpeedModifierMixin {
    @ModifyArg(method = "initCache", at = @At(value = "INVOKE", target = "com/tacz/guns/api/modifier/CacheValue.<init>(Ljava/lang/Object;)V"))
    private Object tacztweaks$initCache$speedModifier(Object value) {
        if (!(value instanceof Float speed)) return value;
        return (float) AttachmentPropertyManager.eval(Config.Modifiers.Speed.INSTANCE.toTaCZ(), speed);
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "getPropertyDiagramsData", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;"))
    private void tacztweaks$getPropertyDiagramsData$speedModifier(ItemStack gunItem, GunData gunData, AttachmentCacheProperty cacheProperty, CallbackInfoReturnable<List<Object>> cir, @Local(ordinal = 0) LocalFloatRef ammoSpeedRef) {
        ammoSpeedRef.set((float) AttachmentPropertyManager.eval(Config.Modifiers.Speed.INSTANCE.toTaCZ(), ammoSpeedRef.get()));
    }
}
