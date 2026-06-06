package me.muksc.tacztweaks.mixin.feature.attribute.handling.sprint_while_reloading;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.gameplay.LocalPlayerSprint;
import me.muksc.tacztweaks.core.extension.DeferredHolderExt;
import me.muksc.tacztweaks.registry.ModAttributes;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerSprint.class, remap = false)
public abstract class LocalPlayerSprintMixin {
    @Shadow @Final private LocalPlayer player;

    @ModifyExpressionValue(method = "getProcessedSprintStatus", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/entity/ReloadState$StateType;isReloading()Z"))
    private boolean tacztweaks$getProcessedSprintStatus$attribute$handling$sprintWhileReloading(boolean original) {
        double value = player.getAttributeValue(DeferredHolderExt.valueOrDelegate(ModAttributes.SPRINT_WHILE_RELOADING));
        return original && value <= 0.0;
    }
}