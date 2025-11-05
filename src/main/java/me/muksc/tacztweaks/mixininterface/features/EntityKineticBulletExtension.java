package me.muksc.tacztweaks.mixininterface.features;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public interface EntityKineticBulletExtension {
    record DamageModifier(
        float flat,
        float multiplier
    ) { }

    ItemStack tacztweaks$getGunStack();

    int tacztweaks$getBlockPierce();

    void tacztweaks$incrementBlockPierce();

    int tacztweaks$getEntityPierce();

    void tacztweaks$incrementEntityPierce();

    void tacztweaks$addDamageModifier(float flat, float multiplier);

    void tacztweaks$popDamageModifier();

    Vec3 tacztweaks$getPosition();

    void tacztweaks$setPosition(Vec3 position);

    int tacztweaks$getBurstIndex();

    void tacztweaks$setBurstIndex(int index);

    int tacztweaks$getPelletIndex();

    void tacztweaks$setPelletIndex(int index);

}
