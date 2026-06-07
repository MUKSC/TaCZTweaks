package me.muksc.tacztweaks.mixin.accessor;

import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = LocalPlayerDataHolder.class, remap = false)
public interface LocalPlayerDataHolderAccessor {
    @Accessor("player")
    LocalPlayer tacztweaks$getPlayer();
}