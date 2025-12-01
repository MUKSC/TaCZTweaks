package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.modifier.custom.AdsModifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import me.muksc.tacztweaks.Config;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = AdsModifier.class, remap = false)
public abstract class AdsModifierMixin {
    @ModifyArg(method = "initCache", at = @At(value = "INVOKE", target = "com/tacz/guns/api/modifier/CacheValue.<init>(Ljava/lang/Object;)V"))
    private Object tacztweaks$initCache$aimTimeModifier(Object value) {
        if (!(value instanceof Float aimTime)) return value;
        return (float) AttachmentPropertyManager.eval(Config.Modifiers.AimTime.INSTANCE.toTaCZ(), aimTime);
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "getPropertyDiagramsData", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;"))
    private void tacztweaks$getPropertyDiagramsData$aimTimeModifier(ItemStack gunItem, GunData gunData, AttachmentCacheProperty cacheProperty, CallbackInfoReturnable<List<IAttachmentModifier.DiagramsData>> cir, @Local(ordinal = 0) LocalFloatRef aimTimeRef) {
        aimTimeRef.set((float) AttachmentPropertyManager.eval(Config.Modifiers.AimTime.INSTANCE.toTaCZ(), aimTimeRef.get()));
    }
}