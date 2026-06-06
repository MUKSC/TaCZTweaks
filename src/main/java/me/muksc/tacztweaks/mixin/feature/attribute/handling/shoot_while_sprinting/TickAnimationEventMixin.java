package me.muksc.tacztweaks.mixin.feature.attribute.handling.shoot_while_sprinting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.client.event.TickAnimationEvent;
import me.muksc.tacztweaks.core.extension.DeferredHolderExt;
import me.muksc.tacztweaks.registry.ModAttributes;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TickAnimationEvent.class, remap = false)
public abstract class TickAnimationEventMixin {
    @SuppressWarnings("MixinExtrasOperationParameters") // MinecraftDev :(
    @WrapOperation(method = "lambda$tickAnimation$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSprinting()Z", remap = true))
    private static boolean tacztweaks$tickAnimation$attribute$handling$shootWhileSprinting$cancelSprintAnimation(LocalPlayer instance, Operation<Boolean> original) {
        double value = instance.getAttributeValue(DeferredHolderExt.valueOrDelegate(ModAttributes.SHOOT_WHILE_SPRINTING));
        if (value <= 0.0) return original.call(instance);
        IClientPlayerGunOperator operator = IClientPlayerGunOperator.fromLocalPlayer(instance);
        return original.call(instance) && operator.getClientShootCoolDown() <= 0;
    }
}