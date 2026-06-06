package me.muksc.tacztweaks.mixin.accessor;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.sounds.ChannelAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChannelAccess.ChannelHandle.class)
public interface ChannelHandleAccessor {
    @Accessor
    @Nullable Channel getChannel();
}