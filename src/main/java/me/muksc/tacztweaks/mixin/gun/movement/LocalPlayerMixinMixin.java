package me.muksc.tacztweaks.mixin.gun.movement;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.tacz.guns.client.gameplay.LocalPlayerReload;
import me.muksc.tacztweaks.Config;
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
    private boolean tacztweaks$swapSprintStatus$sprintWhileReloading(LocalPlayerReload instance) {
        return !Config.Gun.INSTANCE.sprintWhileReloading();
    }
}
