package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.api.client.animation.ObjectAnimationSoundChannel;
import com.tacz.guns.client.sound.GunSoundInstance;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.network.NetworkHandler;
import me.muksc.tacztweaks.network.message.ClientMessageBroadcastSound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ObjectAnimationSoundChannel.class, remap = false)
public abstract class ObjectAnimationSoundChannelMixin {
    @WrapOperation(method = "playSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private GunSoundInstance tacztweaks$playSound$broadcast(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.Tweaks.INSTANCE.audibleFirstPersonGunSounds()) NetworkHandler.INSTANCE.sendC2S(new ClientMessageBroadcastSound(name, volume, pitch, distance));
        return original.call(entity, name, volume, pitch, distance);
    }
}
