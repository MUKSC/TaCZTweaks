package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.util.block.ProjectileExplosion;
import me.muksc.tacztweaks.Config;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ProjectileExplosion.class, remap = false)
public abstract class ProjectileExplosionMixin {
    @WrapOperation(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean tacztweaks$explode$playerDamageModifier(Entity instance, DamageSource pSource, float pAmount, Operation<Boolean> original) {
        if (!(instance instanceof Player)) return original.call(instance, pSource, pAmount);
        return original.call(instance, pSource, (float) AttachmentPropertyManager.eval(Config.Modifiers.INSTANCE.playerDamage(), pAmount));
    }
}
