package me.muksc.tacztweaks.mixin.feature.synced_slide;

import me.muksc.tacztweaks.mixininterface.feature.synced_slide.SlideDataHolder;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements SlideDataHolder {
    @Unique
    private boolean tacztweaks$shouldSlide = false;

    @Override
    public boolean tacztweaks$getShouldSlide() {
        return tacztweaks$shouldSlide;
    }

    @Override
    public void tacztweaks$setShouldSlide(boolean shouldSlide) {
        tacztweaks$shouldSlide = shouldSlide;
    }
}