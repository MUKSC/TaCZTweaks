package me.muksc.tacztweaks.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.client.gameplay.LocalPlayerShoot;
import me.muksc.tacztweaks.Config;
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

    @ModifyExpressionValue(method = "shoot", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/entity/IGunOperator;getSynSprintTime()F"))
    private float tacztweaks$shoot$shootWhileSprinting(float original) {
        return 0.0F;
    }

    @Inject(method = "shoot", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gameplay/LocalPlayerShoot;doShoot(Lcom/tacz/guns/client/resource/GunDisplayInstance;Lcom/tacz/guns/api/item/IGun;Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;J)V"))
    private void tacztweaks$shoot$stopSprintingOnShot(CallbackInfoReturnable<ShootResult> cir) {
        if (Config.Gun.INSTANCE.shootWhileSprinting()) return;
        player.setSprinting(false);
    }
}
