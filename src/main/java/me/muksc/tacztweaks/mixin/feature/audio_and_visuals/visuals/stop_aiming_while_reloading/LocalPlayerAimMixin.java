package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.visuals.stop_aiming_while_reloading;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.client.gameplay.LocalPlayerAim;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.client.player.LocalPlayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerAim.class, remap = false)
public abstract class LocalPlayerAimMixin {
    @Shadow @Final private LocalPlayer player;

    @ModifyExpressionValue(method = "aimProgressCalculate", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/tacz/guns/client/gameplay/LocalPlayerDataHolder;clientIsAiming:Z"))
    private boolean tacztweaks$aimProgressCalculate$stopAimingWhileReloading(boolean original) {
        if (!Config.AudioAndVisuals.Visuals.stopAimingWhileReloading()) return original;
        ReloadState state = IGunOperator.fromLivingEntity(player).getSynReloadState();
        ReloadState.StateType type = state.getStateType();
        return original && (!type.isReloading() || (type.isReloadFinishing() && state.getCountDown() < 500L));
    }
}