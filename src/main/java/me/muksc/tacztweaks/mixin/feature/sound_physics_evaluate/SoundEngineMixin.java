package me.muksc.tacztweaks.mixin.feature.sound_physics_evaluate;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.Channel;
import me.muksc.tacztweaks.feature.sound_physics_evaluate.SoundPhysicsEvaluationSoundInstance;
import me.muksc.tacztweaks.feature.sound_physics_evaluate.SoundPhysicsManager;
import me.muksc.tacztweaks.mixin.accessor.ChannelHandleAccessor;
import me.muksc.tacztweaks.mixininterface.feature.sound_physics_evaluate.ChannelExtraContext;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SoundEngine.class, remap = false)
public abstract class SoundEngineMixin {
    @Definition(id = "ChannelHandle", type = ChannelAccess.ChannelHandle.class)
    @Definition(id = "join", method = "Ljava/util/concurrent/CompletableFuture;join()Ljava/lang/Object;")
    @Expression("? = @((ChannelHandle) ?.join())")
    @ModifyExpressionValue(method = "play", at = @At("MIXINEXTRAS:EXPRESSION"))
    private ChannelAccess.ChannelHandle tacztweaks$play$soundPhysicsEvaluate$context(
        @Nullable ChannelAccess.ChannelHandle original,
        @Local(argsOnly = true) SoundInstance sound
    ) {
        if (original == null) return original;
        ChannelHandleAccessor accessor = (ChannelHandleAccessor) original;
        Channel channel = accessor.getChannel();
        if (channel == null) return original;
        ChannelExtraContext.of(channel).tacztweaks$setSoundInstance(sound);
        return original;
    }

    @SuppressWarnings("MixinExtrasOperationParameters") // MinecraftDev :(
    //? if fabric {
    /*@WrapOperation(method = "method_19752", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/Channel;play()V"))
    *///?} else {
    @WrapOperation(method = "lambda$play$6", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/Channel;play()V"))
    //?}
    private static void tacztweaks$play$soundPhysicsEvaluate$1(Channel instance, Operation<Void> original) {
        SoundInstance soundInstance = ChannelExtraContext.of(instance).tacztweaks$getSoundInstance();
        if (!(soundInstance instanceof SoundPhysicsEvaluationSoundInstance sound)) {
            original.call(instance);
            return;
        }

        SoundPhysicsManager.runProcessing(sound, () -> original.call(instance));
    }

    @SuppressWarnings("MixinExtrasOperationParameters") // MinecraftDev :(
    //? if fabric {
    /*@WrapOperation(method = "method_19755", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/Channel;play()V"))
    *///?} else {
    @WrapOperation(method = "lambda$play$8", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/Channel;play()V"))
    //?}
    private static void tacztweaks$play$soundPhysicsEvaluate$2(Channel instance, Operation<Void> original) {
        SoundInstance soundInstance = ChannelExtraContext.of(instance).tacztweaks$getSoundInstance();
        if (!(soundInstance instanceof SoundPhysicsEvaluationSoundInstance sound)) {
            original.call(instance);
            return;
        }

        SoundPhysicsManager.runProcessing(sound, () -> original.call(instance));
    }
}