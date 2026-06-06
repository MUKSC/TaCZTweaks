package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.audio.broadcast_first_person_gun_sounds;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.client.sound.GunSoundInstance;
import com.tacz.guns.client.sound.SoundPlayManager;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.network.NetworkManager;
import me.muksc.tacztweaks.network.message.ClientMessageBroadcastSound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SoundPlayManager.class, remap = false)
public abstract class SoundPlayManagerMixin {
    @WrapOperation(method = "lambda$playerRefitSound$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playerRefitSound$broadcastFirstPersonGunSounds(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.AudioAndVisuals.Audio.broadcastFirstPersonGunSounds() && name != null) NetworkManager.sendC2S(
            new ClientMessageBroadcastSound(name, volume, pitch, distance)
        );
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playDryFireSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playDryFireSound$broadcastFirstPersonGunSounds(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.AudioAndVisuals.Audio.broadcastFirstPersonGunSounds() && name != null) NetworkManager.sendC2S(
            new ClientMessageBroadcastSound(name, volume, pitch, distance)
        );
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playReloadSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playReloadSound$broadcastFirstPersonGunSounds(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.AudioAndVisuals.Audio.broadcastFirstPersonGunSounds() && name != null) NetworkManager.sendC2S(
            new ClientMessageBroadcastSound(name, volume, pitch, distance)
        );
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playInspectSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playInspectSound$broadcastFirstPersonGunSounds(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.AudioAndVisuals.Audio.broadcastFirstPersonGunSounds() && name != null) NetworkManager.sendC2S(
            new ClientMessageBroadcastSound(name, volume, pitch, distance)
        );
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playBoltSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playBoltSound$broadcastFirstPersonGunSounds(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.AudioAndVisuals.Audio.broadcastFirstPersonGunSounds() && name != null) NetworkManager.sendC2S(
            new ClientMessageBroadcastSound(name, volume, pitch, distance)
        );
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playDrawSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playDrawSound$broadcastFirstPersonGunSounds(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.AudioAndVisuals.Audio.broadcastFirstPersonGunSounds() && name != null) NetworkManager.sendC2S(
            new ClientMessageBroadcastSound(name, volume, pitch, distance)
        );
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playPutAwaySound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playPutAwaySound$broadcastFirstPersonGunSounds(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.AudioAndVisuals.Audio.broadcastFirstPersonGunSounds() && name != null) NetworkManager.sendC2S(
            new ClientMessageBroadcastSound(name, volume, pitch, distance)
        );
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playFireSelectSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playFireSelectSound$broadcastFirstPersonGunSounds(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.AudioAndVisuals.Audio.broadcastFirstPersonGunSounds() && name != null) NetworkManager.sendC2S(
            new ClientMessageBroadcastSound(name, volume, pitch, distance)
        );
        return original.call(entity, name, volume, pitch, distance);
    }
    @WrapOperation(method = "playMeleeBayonetSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playMeleeBayonetSound$broadcastFirstPersonGunSounds(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.AudioAndVisuals.Audio.broadcastFirstPersonGunSounds() && name != null) NetworkManager.sendC2S(
            new ClientMessageBroadcastSound(name, volume, pitch, distance)
        );
        return original.call(entity, name, volume, pitch, distance);
    }
    @WrapOperation(method = "playMeleePushSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playMeleePushSound$broadcastFirstPersonGunSounds(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.AudioAndVisuals.Audio.broadcastFirstPersonGunSounds() && name != null) NetworkManager.sendC2S(
            new ClientMessageBroadcastSound(name, volume, pitch, distance)
        );
        return original.call(entity, name, volume, pitch, distance);
    }
    @WrapOperation(method = "playMeleeStockSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playMeleeStockSound$broadcastFirstPersonGunSounds(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.AudioAndVisuals.Audio.broadcastFirstPersonGunSounds() && name != null) NetworkManager.sendC2S(
            new ClientMessageBroadcastSound(name, volume, pitch, distance)
        );
        return original.call(entity, name, volume, pitch, distance);
    }
}