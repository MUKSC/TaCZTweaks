package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.audio.force_first_person_shooting_sound;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.sound.SoundManager;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SoundPlayManager.class, remap = false)
public abstract class SoundPlayManagerMixin {
    @Definition(id = "SHOOT_3P_SOUND", field = "Lcom/tacz/guns/sound/SoundManager;SHOOT_3P_SOUND:Ljava/lang/String;")
    @Definition(id = "equals", method = "Ljava/lang/String;equals(Ljava/lang/Object;)Z")
    @Expression("SHOOT_3P_SOUND.equals(?)")
    @WrapOperation(method = "lambda$playMessageSound$1", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean tacztweaks$playMessageSound$forceFirstPersonShootingSound$mono(String instance, Object anObject, Operation<Boolean> original) {
        if (!Config.AudioAndVisuals.Audio.forceFirstPersonShootingSound()) return original.call(instance, anObject);
        return original.call(instance, anObject)
            || SoundManager.SHOOT_SOUND.equals(anObject)
            || SoundManager.SILENCE_SOUND.equals(anObject);
    }
}