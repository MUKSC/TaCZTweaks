package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.sound.SoundManager;
import me.muksc.tacztweaks.Config;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public abstract class ModernKineticGunScriptAPIMixin {
    @ModifyExpressionValue(method = "lambda$shootOnce$2", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/tacz/guns/sound/SoundManager;SILENCE_3P_SOUND:Ljava/lang/String;"))
    private String tacztweaks$shootOnce$forceFirstPersonShootSound$silenced(String original) {
        if (!Config.Tweaks.INSTANCE.forceFirstPersonShootingSound()) return original;
        return SoundManager.SILENCE_SOUND;
    }

    @ModifyExpressionValue(method = "lambda$shootOnce$2", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/tacz/guns/sound/SoundManager;SHOOT_3P_SOUND:Ljava/lang/String;"))
    private String tacztweaks$shootOnce$forceFirstPersonShootSound$normal(String original) {
        if (!Config.Tweaks.INSTANCE.forceFirstPersonShootingSound()) return original;
        return SoundManager.SHOOT_SOUND;
    }
}
