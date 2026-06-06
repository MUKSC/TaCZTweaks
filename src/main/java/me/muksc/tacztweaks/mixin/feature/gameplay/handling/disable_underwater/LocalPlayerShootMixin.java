package me.muksc.tacztweaks.mixin.feature.gameplay.handling.disable_underwater;

import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.client.gameplay.LocalPlayerShoot;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LocalPlayerShoot.class, remap = false)
public abstract class LocalPlayerShootMixin {
    @Shadow @Final private LocalPlayer player;

    //~ if neoforge || fabric 'Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z' -> 'Lcom/tacz/guns/api/event/common/GunShootEvent;isCanceled()Z'
    @Inject(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"), cancellable = true)
    private void tacztweaks$shoot$disableUnderwater(CallbackInfoReturnable<ShootResult> cir) {
        if (!Config.Gameplay.Handling.disableUnderwater()) return;
        if (player.isUnderWater()) cir.setReturnValue(ShootResult.FORGE_EVENT_CANCEL);
    }
}