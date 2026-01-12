package me.muksc.tacztweaks.mixin.features.bullet_sounds;

import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.TacHitResult;
import me.muksc.tacztweaks.data.manager.BulletSoundsManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin {
    @Unique
    private final List<ServerPlayer> tacztweaks$hitPlayers = new ArrayList<>();

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void tacztweaks$onHitEntity$onHitPlayer(TacHitResult result, Vec3 startVec, Vec3 endVec, CallbackInfo ci) {
        if (!(result.getEntity() instanceof ServerPlayer player)) return;
        tacztweaks$hitPlayers.add(player);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/entity/EntityKineticBullet;onBulletTick()V", shift = At.Shift.AFTER, remap = false), remap = true)
    private void tacztweaks$tick$tick(CallbackInfo ci) {
        EntityKineticBullet instance = EntityKineticBullet.class.cast(this);
        Level level = instance.level();
        if (!(level instanceof ServerLevel serverLevel)) return;
        BulletSoundsManager.INSTANCE.handleConstant(serverLevel, instance);
        BulletSoundsManager.INSTANCE.handleSoundWhizz(serverLevel, instance, tacztweaks$hitPlayers);
    }
}
