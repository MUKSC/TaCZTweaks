package me.muksc.tacztweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.entity.EntityKineticBullet;
import me.muksc.tacztweaks.FlatDamageModifierHolder;
import me.muksc.tacztweaks.TaCZTweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityKineticBullet.class)
public abstract class EntityKineticBulletMixin implements FlatDamageModifierHolder {
    @Unique
    private float tacztweaks$flatDamageModifier;

    @Override
    public float tacztweaks$getFlatDamageModifier() {
        return tacztweaks$flatDamageModifier;
    }

    @Override
    public void tacztweaks$setFlatDamageModifier(float flatDamageModifier) {
        tacztweaks$flatDamageModifier = flatDamageModifier;
    }

    @ModifyExpressionValue(method = "getDamage", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/ExtraDamage$DistanceDamagePair;getDamage()F", remap = false), remap = false)
    private float applyFlatDamageModifier(float original) {
        return original + tacztweaks$flatDamageModifier;
    }

    @Inject(method = "onBulletTick", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/block/BlockRayTrace;rayTraceBlocks(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;", remap = false), remap = false)
    private void setInstance(CallbackInfo ci) {
        TaCZTweaks.ammoInstance = (EntityKineticBullet)(Object) this;
    }
}
