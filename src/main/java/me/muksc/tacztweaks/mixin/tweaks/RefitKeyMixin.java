package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.client.input.RefitKey;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RefitKey.class, remap = false)
public abstract class RefitKeyMixin {
    @ModifyExpressionValue(method = "onRefitPress", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;hasAttachmentLock(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean tacztweaks$onRefitPress$disableRefitOnAdventure(boolean original, @Local LocalPlayer player) {
        if (!Config.Tweaks.INSTANCE.disableRefitOnAdventure()) return original;
        MultiPlayerGameMode mode = Minecraft.getInstance().gameMode;
        if (mode == null) return original;
        return original || mode.getPlayerMode() == GameType.ADVENTURE;
    }
}
