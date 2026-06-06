package me.muksc.tacztweaks.mixin.feature.balancing.player_damage;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.TacHitResult;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin {
    @Definition(id = "damage", local = @Local(type = float.class, name = "damage"))
    @Definition(id = "getDamage", method = "Lcom/tacz/guns/entity/EntityKineticBullet;getDamage(Lnet/minecraft/world/phys/Vec3;)F")
    @Expression("damage = @(this.getDamage(?))")
    @ModifyExpressionValue(method = "onHitEntity", at = @At("MIXINEXTRAS:EXPRESSION"))
    private float tacztweaks$onHitEntity$playerDamageModifier(
        float original,
        @Local(argsOnly = true) TacHitResult result
    ) {
        if (!(result.getEntity() instanceof Player)) return original;
        return (float) Config.Balancing.PlayerDamage.eval(original);
    }
}