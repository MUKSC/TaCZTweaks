package me.muksc.tacztweaks.mixin.tweaks;

import me.muksc.tacztweaks.mixininterface.tweaks.TaCZResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin implements TaCZResourceLocation {
    @Unique
    private boolean tacztweaks$monoAudio = false;

    @Override
    public boolean tacztweaks$getMonoAudio() {
        return tacztweaks$monoAudio;
    }

    @Override
    public void tacztweaks$setMonoAudio(boolean monoAudio) {
        tacztweaks$monoAudio = monoAudio;
    }
}