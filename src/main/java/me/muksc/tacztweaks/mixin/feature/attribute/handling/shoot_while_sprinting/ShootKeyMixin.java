package me.muksc.tacztweaks.mixin.feature.attribute.handling.shoot_while_sprinting;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.client.input.ShootKey;
import me.muksc.tacztweaks.core.extension.DeferredHolderExt;
import me.muksc.tacztweaks.registry.ModAttributes;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ShootKey.class, remap = false)
public abstract class ShootKeyMixin {
    @Definition(id = "stopSprint", field = "Lcom/tacz/guns/client/gameplay/LocalPlayerSprint;stopSprint:Z")
    @Expression("stopSprint = true")
    @WrapWithCondition(method = "autoShoot", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean tacztweaks$autoShoot$attribute$handling$shootWhileSprinting(
        boolean value,
        @Local LocalPlayer player
    ) {
        double attributeValue = player.getAttributeValue(DeferredHolderExt.valueOrDelegate(ModAttributes.SHOOT_WHILE_SPRINTING));
        return attributeValue <= 0.0;
    }
}