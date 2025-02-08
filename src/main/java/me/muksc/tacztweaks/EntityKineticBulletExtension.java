package me.muksc.tacztweaks;

import net.minecraft.world.item.ItemStack;

public interface EntityKineticBulletExtension {
    record DamageModifier(
        double distance,
        float flat,
        float multiplier
    ) { }

    ItemStack tacztweaks$getGunStack();

    int tacztweaks$getBlockPierce();

    void tacztweaks$setBlockPierce(int blockPierce);

    void tacztweaks$addDamageModifier(double distance, float flat, float multiplier);
}
