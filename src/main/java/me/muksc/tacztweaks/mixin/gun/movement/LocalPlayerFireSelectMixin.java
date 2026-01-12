package me.muksc.tacztweaks.mixin.gun.movement;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.client.gameplay.LocalPlayerFireSelect;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.mixin.accessor.LocalPlayerShootAccessor;
import net.minecraft.client.player.LocalPlayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerFireSelect.class, remap = false)
public abstract class LocalPlayerFireSelectMixin {
    @Shadow @Final private LocalPlayer player;
    @Shadow @Final private LocalPlayerDataHolder data;

    @ModifyExpressionValue(method = "fireSelect", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/tacz/guns/client/gameplay/LocalPlayerDataHolder;clientStateLock:Z"))
    private boolean tacztweaks$fireSelect$fireSelectWhileShoot(boolean original) {
        if (!Config.Gun.INSTANCE.fireSelectWhileShooting()) return original;
        IGunOperator operator = IGunOperator.fromLivingEntity(player);
        if (data.lockedCondition == LocalPlayerShootAccessor.getShootLockedCondition()) return false;
        if (data.lockedCondition == null && !operator.getSynReloadState().getStateType().isReloading()
            && operator.getSynDrawCoolDown() <= 0
            && !operator.getSynIsBolting()
            && operator.getSynMeleeCoolDown() <= 0L) return false;
        return original;
    }
}
