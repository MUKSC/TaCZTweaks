package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.audio.force_first_person_shooting_sound;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.sound.SoundManager;
import me.muksc.tacztweaks.config.Config;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public abstract class ModernKineticGunScriptAPIMixin {
    @ModifyExpressionValue(method = "lambda$shootOnce$2", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/tacz/guns/sound/SoundManager;SILENCE_3P_SOUND:Ljava/lang/String;"))
    private String tacztweaks$shootOnce$forceFirstPersonShootingSound$silenced(String original) {
        return Config.AudioAndVisuals.Audio.forceFirstPersonShootingSound()
            ? SoundManager.SILENCE_SOUND
            : original;
    }

    @ModifyExpressionValue(method = "lambda$shootOnce$2", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/tacz/guns/sound/SoundManager;SHOOT_3P_SOUND:Ljava/lang/String;"))
    private String tacztweaks$shootOnce$forceFirstPersonShootingSound$normal(String original) {
        return Config.AudioAndVisuals.Audio.forceFirstPersonShootingSound()
            ? SoundManager.SHOOT_SOUND
            : original;
    }
}