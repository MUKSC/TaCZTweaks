package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.client.sound.GunSoundInstance;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.sound.SoundManager;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.network.NetworkHandler;
import me.muksc.tacztweaks.network.message.ClientMessageBroadcastSound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SoundPlayManager.class, remap = false)
public abstract class SoundPlayManagerMixin {
    @WrapWithCondition(method = "playHeadHitSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static boolean tacztweaks$playHeadHitSound$conditional(Entity entity, ResourceLocation name, float volume, float pitch, int distance) {
        return !Config.Tweaks.INSTANCE.suppressHeadHitSounds();
    }

    @WrapWithCondition(method = "playFleshHitSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static boolean tacztweaks$playFleshHitSound$conditional(Entity entity, ResourceLocation name, float volume, float pitch, int distance) {
        return !Config.Tweaks.INSTANCE.suppressFleshHitSounds();
    }

    @WrapWithCondition(method = "playKillSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static boolean tacztweaks$playKillSound$conditional(Entity entity, ResourceLocation name, float volume, float pitch, int distance) {
        return !Config.Tweaks.INSTANCE.suppressKillSounds();
    }

    @Definition(id = "SHOOT_3P_SOUND", field = "Lcom/tacz/guns/sound/SoundManager;SHOOT_3P_SOUND:Ljava/lang/String;")
    @Definition(id = "equals", method = "Ljava/lang/String;equals(Ljava/lang/Object;)Z")
    @Expression("SHOOT_3P_SOUND.equals(?)")
    @WrapOperation(method = "lambda$playMessageSound$1", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean tacztweaks$playMessageSound$monoSounds(String instance, Object o, Operation<Boolean> original) {
        if (!Config.Tweaks.INSTANCE.betterMonoConversion()) return original.call(instance, o);
        return original.call(instance, o) || SoundManager.SHOOT_SOUND.equals(o)
            || SoundManager.SILENCE_SOUND.equals(o);
    }

    @WrapOperation(method = "lambda$playerRefitSound$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playerRefitSound$broadcast(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.Tweaks.INSTANCE.audibleFirstPersonGunSounds() && name != null) NetworkHandler.INSTANCE.sendC2S(new ClientMessageBroadcastSound(name, volume, pitch, distance));
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playDryFireSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playDryFireSound$broadcast(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.Tweaks.INSTANCE.audibleFirstPersonGunSounds() && name != null) NetworkHandler.INSTANCE.sendC2S(new ClientMessageBroadcastSound(name, volume, pitch, distance));
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playReloadSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playReloadSound$broadcast(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.Tweaks.INSTANCE.audibleFirstPersonGunSounds() && name != null) NetworkHandler.INSTANCE.sendC2S(new ClientMessageBroadcastSound(name, volume, pitch, distance));
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playInspectSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playInspectSound$broadcast(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.Tweaks.INSTANCE.audibleFirstPersonGunSounds() && name != null) NetworkHandler.INSTANCE.sendC2S(new ClientMessageBroadcastSound(name, volume, pitch, distance));
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playBoltSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playBoltSound$broadcast(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.Tweaks.INSTANCE.audibleFirstPersonGunSounds() && name != null) NetworkHandler.INSTANCE.sendC2S(new ClientMessageBroadcastSound(name, volume, pitch, distance));
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playDrawSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playDrawSound$broadcast(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.Tweaks.INSTANCE.audibleFirstPersonGunSounds() && name != null) NetworkHandler.INSTANCE.sendC2S(new ClientMessageBroadcastSound(name, volume, pitch, distance));
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playPutAwaySound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playPutAwaySound$broadcast(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.Tweaks.INSTANCE.audibleFirstPersonGunSounds() && name != null) NetworkHandler.INSTANCE.sendC2S(new ClientMessageBroadcastSound(name, volume, pitch, distance));
        return original.call(entity, name, volume, pitch, distance);
    }

    @WrapOperation(method = "playFireSelectSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playFireSelectSound$broadcast(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.Tweaks.INSTANCE.audibleFirstPersonGunSounds() && name != null) NetworkHandler.INSTANCE.sendC2S(new ClientMessageBroadcastSound(name, volume, pitch, distance));
        return original.call(entity, name, volume, pitch, distance);
    }
    @WrapOperation(method = "playMeleeBayonetSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playMeleeBayonetSound$broadcast(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.Tweaks.INSTANCE.audibleFirstPersonGunSounds() && name != null) NetworkHandler.INSTANCE.sendC2S(new ClientMessageBroadcastSound(name, volume, pitch, distance));
        return original.call(entity, name, volume, pitch, distance);
    }
    @WrapOperation(method = "playMeleePushSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playMeleePushSound$broadcast(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.Tweaks.INSTANCE.audibleFirstPersonGunSounds() && name != null) NetworkHandler.INSTANCE.sendC2S(new ClientMessageBroadcastSound(name, volume, pitch, distance));
        return original.call(entity, name, volume, pitch, distance);
    }
    @WrapOperation(method = "playMeleeStockSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/sound/SoundPlayManager;playClientSound(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/resources/ResourceLocation;FFI)Lcom/tacz/guns/client/sound/GunSoundInstance;"))
    private static GunSoundInstance tacztweaks$playMeleeStockSound$broadcast(Entity entity, ResourceLocation name, float volume, float pitch, int distance, Operation<GunSoundInstance> original) {
        if (Config.Tweaks.INSTANCE.audibleFirstPersonGunSounds() && name != null) NetworkHandler.INSTANCE.sendC2S(new ClientMessageBroadcastSound(name, volume, pitch, distance));
        return original.call(entity, name, volume, pitch, distance);
    }
}
