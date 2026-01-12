package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.entity.EntityKineticBullet;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin {
    @Definition(id = "USE_MAGIC_DAMAGE_ON", field = "Lcom/tacz/guns/entity/EntityKineticBullet;USE_MAGIC_DAMAGE_ON:Lnet/minecraft/tags/TagKey;")
    @Definition(id = "is", method = "Lnet/minecraft/world/entity/EntityType;is(Lnet/minecraft/tags/TagKey;)Z", remap = true)
    @Expression("?.is(USE_MAGIC_DAMAGE_ON)")
    @ModifyExpressionValue(method = "createDamageSources", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$createDamageSources$dontUseMagicOnEndermen(boolean original, @Local(argsOnly = true) EntityKineticBullet.MaybeMultipartEntity parts) {
        if (!Config.Tweaks.INSTANCE.endermenEvadeBullets()) return original;
        if (parts.hitPart().getType() != EntityType.ENDERMAN) return original;
        return false;
    }
}
