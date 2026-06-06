package me.muksc.tacztweaks.mixin.feature.gameplay.handling.manual_bolting;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.client.gameplay.LocalPlayerBolt;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.config.Config.Gameplay.Handling.EManualBoltingType;
import me.muksc.tacztweaks.mixininterface.feature.gameplay.handling.manual_bolting.ManualBoltingData;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerBolt.class, remap = false)
public abstract class LocalPlayerBoltMixin {
    @Shadow @Final private LocalPlayer player;
    @Shadow @Final private LocalPlayerDataHolder data;

    @Shadow public abstract void bolt();

    @WrapWithCondition(method = "tickAutoBolt", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerBolt;bolt()V"))
    private boolean tacztweaks$tickAutoBolt$manualBolting(LocalPlayerBolt instance) {
        return Config.Gameplay.Handling.manualBolting() == EManualBoltingType.DISABLED;
    }

    @Inject(method = "tickAutoBolt", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerBolt;bolt()V", shift = At.Shift.AFTER))
    private void tacztweaks$tickAutoBolt$manualBolting$boltBeforeReload(CallbackInfo ci) {
        if (Config.Gameplay.Handling.manualBolting() == EManualBoltingType.DISABLED) return;
        ManualBoltingData ext = ManualBoltingData.of(data);
        if (!ext.tacztweaks$getBoltBeforeReload()) return;

        bolt();
        if (!data.isBolting && !data.clientStateLock) {
            ext.tacztweaks$setBoltBeforeReload(false);
            IClientPlayerGunOperator.fromLocalPlayer(player).reload();
        }
    }
}