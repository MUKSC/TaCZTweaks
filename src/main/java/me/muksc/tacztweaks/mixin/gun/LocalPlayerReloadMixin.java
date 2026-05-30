package me.muksc.tacztweaks.mixin.gun;

import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.client.gameplay.LocalPlayerReload;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.mixininterface.gun.LocalPlayerDataHolderExtension;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerReload.class, remap = false)
public abstract class LocalPlayerReloadMixin {
    @Shadow @Final private LocalPlayerDataHolder data;
    @Shadow @Final private LocalPlayer player;

    @Inject(method = "lambda$reload$2", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerDataHolder;lockState(Ljava/util/function/Predicate;)V"), cancellable = true)
    private void tacztweaks$reload$boltBeforeReload(CallbackInfo ci, @Local(argsOnly = true) ItemStack mainHandItem, @Local(argsOnly = true) AbstractGunItem gunItem) {
        if (!Config.Gun.INSTANCE.manualBolting()) return;
        LocalPlayerDataHolderExtension ext = (LocalPlayerDataHolderExtension) data;
        boolean shouldBoltBeforeReload = ext.tacztweaks$getBoltBeforeReload();
        if (!shouldBoltBeforeReload) {
            // https://github.com/MCModderAnchor/TACZ/blob/1.1.8-release/src/main/java/com/tacz/guns/client/gameplay/LocalPlayerBolt.java#L44-L66
            ClientGunIndex index = TimelessAPI.getClientGunIndex(gunItem.getGunId(mainHandItem)).orElse(null);
            if (index != null && index.getGunData() != null) {
                GunData gunData = index.getGunData();
                IGunOperator gunOperator = IGunOperator.fromLivingEntity(player);
                Bolt boltType = gunData.getBolt();
                boolean useInventoryAmmo = gunItem.useInventoryAmmo(mainHandItem);
                boolean hasAmmoInBarrel = gunItem.hasBulletInBarrel(mainHandItem) && boltType != Bolt.OPEN_BOLT;
                boolean hasInventoryAmmo = gunItem.hasInventoryAmmo(player, mainHandItem, gunOperator.needCheckAmmo());
                boolean noAmmo = useInventoryAmmo && !hasInventoryAmmo || !useInventoryAmmo && gunItem.getCurrentAmmoCount(mainHandItem) < 1;
                shouldBoltBeforeReload = boltType == Bolt.MANUAL_ACTION && !hasAmmoInBarrel && !noAmmo;
            }
        }

        if (shouldBoltBeforeReload) {
            ext.tacztweaks$setBoltBeforeReload(true);
            ci.cancel();
        }
    }
}