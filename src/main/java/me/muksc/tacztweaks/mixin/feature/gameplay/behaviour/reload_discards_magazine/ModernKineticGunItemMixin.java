package me.muksc.tacztweaks.mixin.feature.gameplay.behaviour.reload_discards_magazine;

import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ModernKineticGunItem.class, remap = false)
public abstract class ModernKineticGunItemMixin extends AbstractGunItem {
    protected ModernKineticGunItemMixin(Properties pProperties) {
        super(pProperties);
    }

    @Inject(method = "startReload", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"))
    private void tacztweaks$startReload$reloadDiscardsMagazine(
        ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter, CallbackInfoReturnable<Boolean> cir,
        @Local ModernKineticGunScriptAPI api
    ) {
        if (!Config.Gameplay.Behaviour.magazineStyleReloading()) return;
        String gunId = getGunId(api.getItemStack()).toString();
        if (Config.Gameplay.Behaviour.magazineStyleReloadingExclusions().contains(gunId)) return;
        setCurrentAmmoCount(api.getItemStack(), 0);
    }
}