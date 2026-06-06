package me.muksc.tacztweaks.mixininterface.feature.balancing.recoil;

import com.tacz.guns.resource.pojo.data.gun.GunRecoil;

import java.util.function.Function;

public interface DynamicGunRecoil {
    static DynamicGunRecoil of(GunRecoil instance) {
        return (DynamicGunRecoil) instance;
    }

    void tacztweaks$setDynamicModifierMapper(Function<Double, Double> mapper);
}