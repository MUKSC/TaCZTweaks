package me.muksc.tacztweaks.mixin.feature.gameplay.handling.fire_select_interrupts_shooting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.client.gameplay.LocalPlayerFireSelect;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.core.extension.TaCZExt;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerFireSelect.class, remap = false)
public abstract class LocalPlayerFireSelectMixin {
    @WrapOperation(method = "fireSelect", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/tacz/guns/client/gameplay/LocalPlayerDataHolder;clientStateLock:Z"))
    private boolean tacztweaks$fireSelect$fireSelectInterruptsShooting(LocalPlayerDataHolder instance, Operation<Boolean> original) {
        return Config.Gameplay.Handling.fireSelectInterruptsShooting()
            ? TaCZExt.getClientStateLockExcludingShoot(instance)
            : original.call(instance);
    }
}