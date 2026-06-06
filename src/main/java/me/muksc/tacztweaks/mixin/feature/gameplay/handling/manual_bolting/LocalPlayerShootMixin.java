package me.muksc.tacztweaks.mixin.feature.gameplay.handling.manual_bolting;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.client.gameplay.LocalPlayerShoot;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.config.Config.Gameplay.Handling.EManualBoltingType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerShoot.class, remap = false)
public abstract class LocalPlayerShootMixin {
    @WrapWithCondition(method = "preCheck", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/client/gameplay/IClientPlayerGunOperator;bolt()V"))
    private boolean tacztweaks$shoot$manualBolting$disableShootToBolt(IClientPlayerGunOperator instance) {
        return Config.Gameplay.Handling.manualBolting() != EManualBoltingType.ENABLED_KEY_ONLY;
    }
}