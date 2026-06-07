package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.system.mix_to_mono;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.sound.GunSoundInstance;
import me.muksc.tacztweaks.mixininterface.feature.audio_and_visuals.system.mix_to_mono.MonoObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GunSoundInstance.class)
public abstract class GunSoundInstanceMixin {
    @Unique
    private boolean tacztweaks$mono = false;

    @Inject(method = "<init>(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/world/entity/Entity;ILnet/minecraft/resources/ResourceLocation;ZZ)V", at = @At("TAIL"), remap = false)
    private void tacztweaks$init$mixToMono$storeMono(SoundEvent soundEvent, SoundSource source, float volume, float pitch, Entity entity, int soundDistance, ResourceLocation registryName, boolean mono, boolean relative, CallbackInfo ci) {
        tacztweaks$mono = mono;
    }

    @ModifyExpressionValue(method = "resolve", at = @At(value = "NEW", target = "com/tacz/guns/client/sound/GunSoundInstance$TaczSound", remap = false))
    private @Coerce Object tacztweaks$resolve$mixToMono$setMono(@Coerce Object original) {
        MonoObject object = MonoObject.of(original);
        object.tacztweaks$setMono(tacztweaks$mono);
        return original;
    }
}