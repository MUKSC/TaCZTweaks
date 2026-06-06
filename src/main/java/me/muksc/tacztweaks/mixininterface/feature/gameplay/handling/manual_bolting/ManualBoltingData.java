package me.muksc.tacztweaks.mixininterface.feature.gameplay.handling.manual_bolting;

import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;

public interface ManualBoltingData {
    static ManualBoltingData of(LocalPlayerDataHolder instance) {
        return (ManualBoltingData) instance;
    }

    boolean tacztweaks$getBoltBeforeReload();

    void tacztweaks$setBoltBeforeReload(boolean boltBeforeReload);
}