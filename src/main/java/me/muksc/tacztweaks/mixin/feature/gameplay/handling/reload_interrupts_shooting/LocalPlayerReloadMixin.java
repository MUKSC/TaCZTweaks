package me.muksc.tacztweaks.mixin.feature.gameplay.handling.reload_interrupts_shooting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.client.gameplay.LocalPlayerReload;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.core.extension.TaCZExt;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerReload.class, remap = false)
public abstract class LocalPlayerReloadMixin {
    @WrapOperation(method = "lambda$reload$2", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/tacz/guns/client/gameplay/LocalPlayerDataHolder;clientStateLock:Z"))
    private boolean tacztweaks$reload$reloadInterruptsShoot(LocalPlayerDataHolder instance, Operation<Boolean> original) {
        return Config.Gameplay.Handling.reloadInterruptsShooting()
            ? TaCZExt.getClientStateLockExcludingShoot(instance)
            : original.call(instance);
    }
}