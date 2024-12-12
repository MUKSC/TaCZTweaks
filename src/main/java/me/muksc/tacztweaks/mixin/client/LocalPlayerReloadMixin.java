package me.muksc.tacztweaks.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.client.gameplay.LocalPlayerReload;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.mixin.accessor.LocalPlayerShootAccessor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = LocalPlayerReload.class, remap = false)
public abstract class LocalPlayerReloadMixin {
    @Shadow @Final private LocalPlayer player;
    @Shadow @Final private LocalPlayerDataHolder data;

    @ModifyArg(method = "doReload", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/animation/statemachine/GunAnimationStateMachine;setNoAmmo(Z)Lcom/tacz/guns/client/animation/statemachine/GunAnimationStateMachine;"), index = 0)
    private boolean setNoAmmo(boolean noAmmo, @Local(argsOnly = true) IGun iGun, @Local(argsOnly = true) ItemStack mainhandItem, @Local Bolt boltType) {
        if (!Config.reloadWhileShooting) return noAmmo;
        int ammoCount = iGun.getCurrentAmmoCount(mainhandItem) + (iGun.hasBulletInBarrel(mainhandItem) && boltType != Bolt.OPEN_BOLT ? 1 : 0);
        return ammoCount <= 0;
    }

    @ModifyExpressionValue(method = "lambda$reload$1", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/tacz/guns/client/gameplay/LocalPlayerDataHolder;clientStateLock:Z"))
    private boolean allowReloadWhileShoot(boolean original, @Local(argsOnly = true) ItemStack mainhandItem, @Local(argsOnly = true) AbstractGunItem gunItem) {
        if (!Config.reloadWhileShooting) return original;
        if (IGunOperator.fromLivingEntity(player).needCheckAmmo() && !gunItem.canReload(player, mainhandItem)) return original;

        IGunOperator operator = IGunOperator.fromLivingEntity(player);
        if (data.lockedCondition == LocalPlayerShootAccessor.getShootLockedCondition()) return false;
        if (data.lockedCondition == null && !operator.getSynReloadState().getStateType().isReloading()
            && operator.getSynDrawCoolDown() <= 0
            && operator.getSynBoltCoolDown() < 0
            && operator.getSynMeleeCoolDown() <= 0L) return false;
        return original;
    }
}
