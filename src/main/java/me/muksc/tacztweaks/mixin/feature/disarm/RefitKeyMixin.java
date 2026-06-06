package me.muksc.tacztweaks.mixin.feature.disarm;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.input.RefitKey;
import me.muksc.tacztweaks.feature.disarm.DisarmManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RefitKey.class, remap = false)
public abstract class RefitKeyMixin {
    @ModifyExpressionValue(method = "onRefitPress", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;hasAttachmentLock(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean tacztweaks$onRefitPress$disarm(boolean original) {
        return original || DisarmManager.shouldDisarm();
    }
}