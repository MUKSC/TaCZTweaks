package me.muksc.tacztweaks.mixininterface.feature.synced_slide;

import net.minecraft.world.entity.LivingEntity;

public interface SlideDataHolder {
    static SlideDataHolder of(LivingEntity instance) {
        return (SlideDataHolder) instance;
    }

    boolean tacztweaks$getShouldSlide();

    void tacztweaks$setShouldSlide(boolean shouldSlide);
}