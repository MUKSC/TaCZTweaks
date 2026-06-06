package me.muksc.tacztweaks.mixin.feature.general.compatibility.mts;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.util.EntityUtil;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.feature.general.compatibility.MTSManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityUtil.class, remap = false)
public abstract class EntityUtilMixin {
    @WrapOperation(method = "getHitResult", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/HitboxHelper;getFixedBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/AABB;"))
    private static AABB tacztweaks$getHitResult$mtsCompat(Entity entity, Entity owner, Operation<AABB> original) {
        if (!Config.General.Compatibility.mtsCompat()) return original.call(entity, owner);
        return MTSManager.getBoundingBox(entity, () -> original.call(entity, owner));
    }
}