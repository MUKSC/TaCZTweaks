package me.muksc.tacztweaks.mixin.gun;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import me.muksc.tacztweaks.Config;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public abstract class ModernKineticGunScriptAPIMixin {
    @Shadow private ResourceLocation gunId;

    @ModifyExpressionValue(method = "getNeededAmmoAmount", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/gun/AbstractGunItem;getCurrentAmmoCount(Lnet/minecraft/world/item/ItemStack;)I"))
    private int tacztweaks$getNeededAmmoAmount$discardCurrentAmmo(int original) {
        if (!Config.Gun.INSTANCE.reloadDiscardsMagazine()) return original;
        if (Config.Gun.INSTANCE.reloadDiscardsMagazineExclusions().contains(gunId.toString())) return original;
        return 0;
    }
}
