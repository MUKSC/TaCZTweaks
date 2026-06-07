package me.muksc.tacztweaks.mixin.feature.sound_physics_evaluate;

import com.mojang.blaze3d.audio.Channel;
import me.muksc.tacztweaks.mixininterface.feature.sound_physics_evaluate.ChannelExtraContext;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Channel.class)
public abstract class ChannelMixin implements ChannelExtraContext {
    @Unique
    private SoundInstance tacztweaks$soundInstance = null;

    @Override
    public SoundInstance tacztweaks$getSoundInstance() {
        return tacztweaks$soundInstance;
    }

    @Override
    public void tacztweaks$setSoundInstance(SoundInstance sound) {
        tacztweaks$soundInstance = sound;
    }
}