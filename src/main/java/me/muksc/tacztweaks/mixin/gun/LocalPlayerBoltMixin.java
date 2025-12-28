package me.muksc.tacztweaks.mixin.gun;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.tacz.guns.client.gameplay.LocalPlayerBolt;
import me.muksc.tacztweaks.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerBolt.class, remap = false)
public abstract class LocalPlayerBoltMixin {
    @WrapWithCondition(method = "tickAutoBolt", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerBolt;bolt()V"))
    private boolean tacztweaks$tickAutoBolt$manualBolt(LocalPlayerBolt instance) {
        return !Config.Gun.INSTANCE.manualBolting();
    }
}
