package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.visuals.stop_aiming_while_bolting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.gameplay.LocalPlayerAim;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import me.muksc.tacztweaks.config.Config;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerAim.class, remap = false)
public abstract class LocalPlayerAimMixin {
    @Shadow @Final private LocalPlayerDataHolder data;

    @ModifyExpressionValue(method = "aimProgressCalculate", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/tacz/guns/client/gameplay/LocalPlayerDataHolder;clientIsAiming:Z"))
    private boolean tacztweaks$aimProgressCalculate$stopAimingWhileBolting(boolean original) {
        return original && (!Config.AudioAndVisuals.Visuals.stopAimingWhileBolting() || !data.isBolting);
    }
}