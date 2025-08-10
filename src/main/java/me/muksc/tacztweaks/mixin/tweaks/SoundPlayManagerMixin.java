package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.sound.SoundManager;
import me.muksc.tacztweaks.Config;
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
            || SoundManager.RELOAD_EMPTY_SOUND.equals(o) || SoundManager.RELOAD_TACTICAL_SOUND.equals(o)
            || SoundManager.SILENCE_SOUND.equals(o);
    }
}
