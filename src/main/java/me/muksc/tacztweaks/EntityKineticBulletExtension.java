package me.muksc.tacztweaks;

import net.minecraft.world.item.ItemStack;

public interface EntityKineticBulletExtension {
    ItemStack tacztweaks$getGunStack();

    int tacztweaks$getBlockPierce();

    void tacztweaks$setBlockPierce(int blockPierce);

    float tacztweaks$getFlatDamageModifier();

    void tacztweaks$setFlatDamageModifier(float flatDamageModifier);

    float tacztweaks$getDamageMultiplier();

    void tacztweaks$setDamageMultiplier(float damageMultiplier);
}
