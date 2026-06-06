package me.muksc.tacztweaks.mixininterface.feature.keyactions.unload;

import com.tacz.guns.api.item.IGun;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface UnloadableGun {
    static UnloadableGun of(IGun instance) {
        return (UnloadableGun) instance;
    }

    void tacztweaks$unload(Player player, ItemStack gunItem);
}