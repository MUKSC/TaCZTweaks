package me.muksc.tacztweaks.mixininterface.feature.sound_physics_evaluate;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.jetbrains.annotations.Nullable;

public interface ChannelExtraContext {
    static ChannelExtraContext of(Channel instance) {
        return (ChannelExtraContext) instance;
    }

    @Nullable SoundInstance tacztweaks$getSoundInstance();

    void tacztweaks$setSoundInstance(SoundInstance sound);
}