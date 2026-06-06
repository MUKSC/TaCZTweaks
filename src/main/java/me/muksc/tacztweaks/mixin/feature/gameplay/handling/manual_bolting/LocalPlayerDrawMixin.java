package me.muksc.tacztweaks.mixin.feature.gameplay.handling.manual_bolting;

import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.client.gameplay.LocalPlayerDraw;
import me.muksc.tacztweaks.mixininterface.feature.gameplay.handling.manual_bolting.ManualBoltingData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerDraw.class, remap = false)
public abstract class LocalPlayerDrawMixin {
    @Shadow
    @Final
    private LocalPlayerDataHolder data;

    @Inject(method = "resetData", at = @At("TAIL"))
    private void tacztweaks$manualBolting$resetData(CallbackInfo ci) {
        ManualBoltingData ext = ManualBoltingData.of(data);
        ext.tacztweaks$setBoltBeforeReload(false);
    }
}