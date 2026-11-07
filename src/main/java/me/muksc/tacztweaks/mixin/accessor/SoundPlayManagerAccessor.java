package me.muksc.tacztweaks.mixin.accessor;

import com.tacz.guns.client.resource.pojo.display.SoundData;
import com.tacz.guns.client.sound.GunSoundInstance;
import com.tacz.guns.client.sound.SoundPlayManager;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = SoundPlayManager.class, remap = false)
public interface SoundPlayManagerAccessor {
    @Invoker("playClientSound")
    static GunSoundInstance invokePlayClientSound(Entity entity, @Nullable SoundData sound, float volume, float pitch, int distance, boolean mono, int concurrencyLimit, boolean trackEntity, boolean relative) {
        throw new AssertionError();
    }
}