package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.audio.force_default_hit_and_kill_sounds;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.GunMod;
import com.tacz.guns.client.resource.pojo.display.SoundData;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.sound.SoundManager;
import me.muksc.tacztweaks.config.Config;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static me.muksc.tacztweaks.core.IdentifierKt.Identifier;

@Mixin(value = SoundPlayManager.class, remap = false)
public abstract class SoundPlayManagerMixin {
    @Unique
    private static final SoundData tacztweaks$defaultHeadHit = SoundData.of(Identifier(GunMod.MOD_ID, SoundManager.HEAD_HIT_SOUND));

    @Unique
    private static final SoundData tacztweaks$defaultFreshHit = SoundData.of(Identifier(GunMod.MOD_ID, SoundManager.FLESH_HIT_SOUND));

    @Unique
    private static final SoundData tacztweaks$defaultKill = SoundData.of(Identifier(GunMod.MOD_ID, SoundManager.KILL_SOUND));

    @Contract("null -> null")
    @ModifyExpressionValue(method = "playHeadHitSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/resource/GunDisplayInstance;getSounds(Ljava/lang/String;)Lcom/tacz/guns/client/resource/pojo/display/SoundData;"))
    private static SoundData tacztweaks$playHeadHitSounds$forceDefaultHitAndKillSounds(SoundData original) {
        return original != null && Config.AudioAndVisuals.Audio.forceDefaultHitAndKillSounds()
            ? tacztweaks$defaultHeadHit
            : original;
    }

    @ModifyExpressionValue(method = "playFleshHitSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/resource/GunDisplayInstance;getSounds(Ljava/lang/String;)Lcom/tacz/guns/client/resource/pojo/display/SoundData;"))
    private static SoundData tacztweaks$playFleshHitSounds$forceDefaultHitAndKillSounds(SoundData original) {
        return original != null && Config.AudioAndVisuals.Audio.forceDefaultHitAndKillSounds()
            ? tacztweaks$defaultFreshHit
            : original;
    }

    @ModifyExpressionValue(method = "playKillSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/resource/GunDisplayInstance;getSounds(Ljava/lang/String;)Lcom/tacz/guns/client/resource/pojo/display/SoundData;"))
    private static SoundData tacztweaks$playKillSounds$forceDefaultHitAndKillSounds(SoundData original) {
        return original != null && Config.AudioAndVisuals.Audio.forceDefaultHitAndKillSounds()
            ? tacztweaks$defaultKill
            : original;
    }
}