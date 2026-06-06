package me.muksc.tacztweaks.mixin.feature.audio_and_visuals.visuals.cancel_inspection;

import com.tacz.guns.api.client.animation.statemachine.LuaAnimationStateMachine;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.animation.statemachine.GunAnimationStateContext;
import com.tacz.guns.client.gameplay.LocalPlayerInspect;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerInspect.class, remap = false)
public abstract class LocalPlayerInspectMixin {
    @Unique
    private long tacztweaks$inspectTimestamp = -1L;

    @Inject(method = "lambda$inspect$0", at = @At("HEAD"), cancellable = true)
    private void tacztweaks$inspect$cancelInspection(GunData gunData, IGun iGun, ItemStack mainHandItem, GunDisplayInstance gunIndex, CallbackInfo ci) {
        if (!Config.AudioAndVisuals.Visuals.cancelInspection()) return;
        long now = System.currentTimeMillis();
        if (now - tacztweaks$inspectTimestamp > 3_000L) {
            tacztweaks$inspectTimestamp = now;
            return;
        }

        LuaAnimationStateMachine<GunAnimationStateContext> state = gunIndex.getAnimationStateMachine();
        if (state == null) return;
        SoundPlayManager.stopPlayGunSound();
        state.trigger("inspect_retreat");
        tacztweaks$inspectTimestamp = -1L;
        ci.cancel();
    }
}