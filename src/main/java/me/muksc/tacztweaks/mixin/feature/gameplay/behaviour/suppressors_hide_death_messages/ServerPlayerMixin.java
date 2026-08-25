package me.muksc.tacztweaks.mixin.feature.gameplay.behaviour.suppressors_hide_death_messages;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.entity.EntityKineticBullet;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.mixininterface.feature.gameplay.behaviour.suppressors_hide_death_messages.BulletWithSuppressor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Definition(id = "getBoolean", method = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z")
    @Definition(id = "RULE_SHOWDEATHMESSAGES", field = "Lnet/minecraft/world/level/GameRules;RULE_SHOWDEATHMESSAGES:Lnet/minecraft/world/level/GameRules$Key;")
    @Expression("?.getBoolean(RULE_SHOWDEATHMESSAGES)")
    @ModifyExpressionValue(method = "die", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$die$suppressorsHideDeathMessages(
        boolean original,
        @Local(argsOnly = true) DamageSource cause
    ) {
        if (!Config.Gameplay.Behaviour.suppressorsHideDeathMessages()) return original;
        if (!(cause.getDirectEntity() instanceof EntityKineticBullet bullet)) return original;
        return original && !BulletWithSuppressor.of(bullet).tacztweaks$isSuppressed();
    }
}