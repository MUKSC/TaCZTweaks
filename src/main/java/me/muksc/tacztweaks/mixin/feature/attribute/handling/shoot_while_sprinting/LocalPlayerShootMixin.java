package me.muksc.tacztweaks.mixin.feature.attribute.handling.shoot_while_sprinting;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.gameplay.LocalPlayerShoot;
import me.muksc.tacztweaks.core.extension.DeferredHolderExt;
import me.muksc.tacztweaks.registry.ModAttributes;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayerShoot.class, remap = false)
public abstract class LocalPlayerShootMixin {
    @Shadow @Final private LocalPlayer player;

    @Definition(id = "getSynSprintTime", method = "Lcom/tacz/guns/api/entity/IGunOperator;getSynSprintTime()F")
    @Expression("?.getSynSprintTime() > 0.0")
    @ModifyExpressionValue(method = "shoot", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$shoot$attribute$handling$shootWhileSprinting(boolean original) {
        double value = player.getAttributeValue(DeferredHolderExt.valueOrDelegate(ModAttributes.SHOOT_WHILE_SPRINTING));
        return original && value <= 0.0;
    }
}