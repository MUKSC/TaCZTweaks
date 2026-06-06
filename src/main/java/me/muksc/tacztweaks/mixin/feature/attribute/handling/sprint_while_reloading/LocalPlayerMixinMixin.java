package me.muksc.tacztweaks.mixin.feature.attribute.handling.sprint_while_reloading;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.tacz.guns.client.gameplay.LocalPlayerReload;
import me.muksc.tacztweaks.core.extension.DeferredHolderExt;
import me.muksc.tacztweaks.registry.ModAttributes;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayer.class, priority = 1500, remap = false)
public abstract class LocalPlayerMixinMixin {
    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.LocalPlayerMixin",
        name = "swapSprintStatus"
    )
    @WrapWithCondition(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerReload;cancelReload()V"))
    private boolean tacztweaks$swapSprintStatus$attribute$handling$sprintWhileReloading(LocalPlayerReload instance) {
        var player = LocalPlayer.class.cast(this);
        double value = player.getAttributeValue(DeferredHolderExt.valueOrDelegate(ModAttributes.SPRINT_WHILE_RELOADING));
        return value <= 0.0;
    }
}