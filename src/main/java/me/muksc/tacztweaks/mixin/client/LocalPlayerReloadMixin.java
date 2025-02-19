package me.muksc.tacztweaks.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.client.gameplay.LocalPlayerReload;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.mixin.accessor.LocalPlayerShootAccessor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerReload.class, remap = false)
public abstract class LocalPlayerReloadMixin {
    @Shadow @Final private LocalPlayer player;
    @Shadow @Final private LocalPlayerDataHolder data;

    @Inject(method = "doReload", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;stopPlayGunSound()V"))
    private void setNoAmmo(IGun iGun, GunDisplayInstance display, GunData gunData, ItemStack mainhandItem, CallbackInfo ci, @Local Bolt boltType, @Local LocalBooleanRef noAmmoRef) {
        if (!Config.reloadWhileShooting) return;
        int ammoCount = iGun.getCurrentAmmoCount(mainhandItem) + (iGun.hasBulletInBarrel(mainhandItem) && boltType != Bolt.OPEN_BOLT ? 1 : 0);
        noAmmoRef.set(ammoCount <= 0);
    }

    @ModifyExpressionValue(method = "lambda$reload$2", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/tacz/guns/client/gameplay/LocalPlayerDataHolder;clientStateLock:Z"))
    private boolean allowReloadWhileShoot(boolean original, @Local(argsOnly = true) ItemStack mainhandItem, @Local(argsOnly = true) AbstractGunItem gunItem) {
        if (!Config.reloadWhileShooting) return original;
        if (IGunOperator.fromLivingEntity(player).needCheckAmmo() && !gunItem.canReload(player, mainhandItem)) return original;

        IGunOperator operator = IGunOperator.fromLivingEntity(player);
        if (data.lockedCondition == LocalPlayerShootAccessor.getShootLockedCondition()) return false;
        if (data.lockedCondition == null && !operator.getSynReloadState().getStateType().isReloading()
            && operator.getSynDrawCoolDown() <= 0
            && !operator.getSynIsBolting()
            && operator.getSynMeleeCoolDown() <= 0L) return false;
        return original;
    }
}
