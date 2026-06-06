package me.muksc.tacztweaks.mixin.feature.general.compatibility.lso;

//? if forge || neoforge {
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.init.ModDamageTypes;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.feature.general.compatibility.LegendarySurvivalOverhaulManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

//~ if neoforge 'sfiomn.legendarysurvivaloverhaul.common.events.CommonForgeEvents' -> 'sfiomn.legendarysurvivaloverhaul.common.events.CommonNeoForgeEvents'
@Mixin(value = sfiomn.legendarysurvivaloverhaul.common.events.CommonForgeEvents.class, remap = false)
public abstract class CommonForgeEventsMixin {
    @WrapOperation(method = "onEntityHurtDamage", at = @At(value = "INVOKE", target = "Lsfiomn/legendarysurvivaloverhaul/util/PlayerModelUtil;getPreciseEntityImpact(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/player/Player;)Ljava/util/List;"))
    private static List<?> tacztweaks$onEntityHurtDamage$lsoCompat(Entity hitEntity, Player player, Operation<List<?>> original) {
        if (!Config.General.Compatibility.lsoCompat()) return original.call(hitEntity, player);
        if (!(hitEntity instanceof EntityKineticBullet bullet)) return original.call(hitEntity, player);
        return LegendarySurvivalOverhaulManager.wrapOperation(bullet, () -> original.call(hitEntity, player));
    }

    @Definition(id = "is", method = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    @Definition(id = "IS_PROJECTILE", field = "Lnet/minecraft/tags/DamageTypeTags;IS_PROJECTILE:Lnet/minecraft/tags/TagKey;", remap = true)
    @Expression("?.is(IS_PROJECTILE)")
    @WrapOperation(method = "onEntityHurtDamage", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean tacztweaks$onEntityHurtDamage$lsoCompat$markAsProjectile(DamageSource instance, TagKey<DamageType> damageTypeKey, Operation<Boolean> original) {
        return original.call(instance, damageTypeKey)
            || (Config.General.Compatibility.lsoCompat() && instance.is(ModDamageTypes.BULLETS_TAG));
    }
}
//?} else {
/*import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public abstract class CommonForgeEventsMixin { }
*///?}