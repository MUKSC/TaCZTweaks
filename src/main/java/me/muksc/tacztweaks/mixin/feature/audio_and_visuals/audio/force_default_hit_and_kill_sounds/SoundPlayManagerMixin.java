package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.audio.force_default_hit_and_kill_sounds;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.GunMod;
import com.tacz.guns.client.sound.SoundPlayManager;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static me.muksc.tacztweaks.core.IdentifierKt.Identifier;

@Mixin(value = SoundPlayManager.class, remap = false)
public abstract class SoundPlayManagerMixin {
    @Unique
    private static final ResourceLocation tacztweaks$defaultHeadHit = Identifier(GunMod.MOD_ID, "head_hit");

    @Unique
    private static final ResourceLocation tacztweaks$defaultFreshHit = Identifier(GunMod.MOD_ID, "flesh_hit");

    @Unique
    private static final ResourceLocation tacztweaks$defaultKill = Identifier(GunMod.MOD_ID, "kill");

    @ModifyExpressionValue(method = "playHeadHitSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/resource/GunDisplayInstance;getSounds(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    private static ResourceLocation tacztweaks$playHeadHitSounds$forceDefaultHitAndKillSounds(ResourceLocation original) {
        return original != null && Config.AudioAndVisuals.Audio.forceDefaultHitAndKillSounds()
            ? tacztweaks$defaultHeadHit
            : original;
    }

    @ModifyExpressionValue(method = "playFleshHitSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/resource/GunDisplayInstance;getSounds(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    private static ResourceLocation tacztweaks$playFleshHitSounds$forceDefaultHitAndKillSounds(ResourceLocation original) {
        return original != null && Config.AudioAndVisuals.Audio.forceDefaultHitAndKillSounds()
            ? tacztweaks$defaultFreshHit
            : original;
    }

    @ModifyExpressionValue(method = "playKillSound", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/resource/GunDisplayInstance;getSounds(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    private static ResourceLocation tacztweaks$playKillSounds$forceDefaultHitAndKillSounds(ResourceLocation original) {
        return original != null && Config.AudioAndVisuals.Audio.forceDefaultHitAndKillSounds()
            ? tacztweaks$defaultKill
            : original;
    }
}