package me.muksc.tacztweaks.mixin.gun.movement;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.gameplay.LocalPlayerSprint;
import me.muksc.tacztweaks.Config;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerSprint.class, remap = false)
public abstract class LocalPlayerSprintMixin {
    @ModifyExpressionValue(method = "getProcessedSprintStatus", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/entity/ReloadState$StateType;isReloading()Z"))
    private boolean tacztweaks$getProcessedSprintStatus$sprintWhileReloading(boolean original) {
        if (!Config.Gun.INSTANCE.sprintWhileReloading()) return original;
        return false;
    }

    @ModifyExpressionValue(method = "getProcessedSprintStatus", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/tacz/guns/client/gameplay/LocalPlayerSprint;stopSprint:Z"))
    private boolean tacztweaks$getProcessedSprintStatus$shootWhileSprinting(boolean original) {
        return original && !Config.Gun.INSTANCE.shootWhileSprinting();
    }
}
