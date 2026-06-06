package me.muksc.tacztweaks.mixininterface.feature.datapack;

import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.world.item.ItemStack;

public interface TaCZTweaksBullet {
    static TaCZTweaksBullet of(EntityKineticBullet instance) {
        return (TaCZTweaksBullet) instance;
    }

    ItemStack tacztweaks$getGunStack();

    int tacztweaks$getBurstIndex();

    void tacztweaks$setBurstIndex(int index);

    int tacztweaks$getPelletIndex();

    void tacztweaks$setPelletIndex(int index);

    int tacztweaks$getBlockPierce();

    void tacztweaks$incrementBlockPierce();

    int tacztweaks$getEntityPierce();

    void tacztweaks$incrementEntityPierce();

    void tacztweaks$modifyDamage(float flat, float multiplier);

    void tacztweaks$modifyEntityHitDamage(float flat, float multiplier);

    record DamageModifier(
        float flat,
        float multiplier
    ) { }
}