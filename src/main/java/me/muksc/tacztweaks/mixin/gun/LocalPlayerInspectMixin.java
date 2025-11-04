package me.muksc.tacztweaks.mixin.gun;

import com.tacz.guns.api.client.animation.statemachine.LuaAnimationStateMachine;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.animation.statemachine.GunAnimationStateContext;
import com.tacz.guns.client.gameplay.LocalPlayerInspect;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import me.muksc.tacztweaks.Config;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayerInspect.class, remap = false)
public abstract class LocalPlayerInspectMixin {
    @Shadow @Final private LocalPlayer player;

    @Unique
    private long tacztweaks$lastInspect = -1L;

    @Inject(method = "lambda$inspect$0", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunData;getBolt()Lcom/tacz/guns/resource/pojo/data/gun/Bolt;"), cancellable = true)
    private void tacztweaks$inspect$cancelInspection(GunData gunData, IGun iGun, ItemStack mainHandItem, GunDisplayInstance gunIndex, CallbackInfo ci) {
        if (!Config.Gun.INSTANCE.cancelInspection()) return;
        long now = player.level().getGameTime();
        if (now - tacztweaks$lastInspect > 3L * 20) {
            tacztweaks$lastInspect = now;
            return;
        }

        LuaAnimationStateMachine<GunAnimationStateContext> state = gunIndex.getAnimationStateMachine();
        SoundPlayManager.stopPlayGunSound();
        state.trigger("inspect_retreat");
        tacztweaks$lastInspect = -1L;
        ci.cancel();
    }
}
