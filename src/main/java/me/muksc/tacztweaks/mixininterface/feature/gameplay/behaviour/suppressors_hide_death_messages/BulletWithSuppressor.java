package me.muksc.tacztweaks.mixininterface.feature.gameplay.behaviour.suppressors_hide_death_messages;

import com.tacz.guns.entity.EntityKineticBullet;

public interface BulletWithSuppressor {
    static BulletWithSuppressor of(EntityKineticBullet instance) {
        return (BulletWithSuppressor) instance;
    }

    boolean tacztweaks$isSuppressed();
}