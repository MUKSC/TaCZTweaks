package me.muksc.tacztweaks.mixin.feature.balancing.player_headshot;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.TacHitResult;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin {
    @ModifyExpressionValue(method = "onHitEntity", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/tacz/guns/entity/EntityKineticBullet;headShot:F"))
    private float tacztweaks$onHitEntity$playerHeadshotModifier(
        float original,
        @Local(argsOnly = true) TacHitResult result
    ) {
        if (!(result.getEntity() instanceof Player)) return original;
        return (float) Config.Balancing.PlayerHeadshot.eval(original);
    }
}