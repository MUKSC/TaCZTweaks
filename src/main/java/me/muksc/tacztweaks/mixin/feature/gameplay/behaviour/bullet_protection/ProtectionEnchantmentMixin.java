package me.muksc.tacztweaks.mixin.feature.gameplay.behaviour.bullet_protection;

//? if 1.20.1 {
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.init.ModDamageTypes;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ProtectionEnchantment.class)
public abstract class ProtectionEnchantmentMixin {
    @Definition(id = "is", method = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
    @Definition(id = "IS_PROJECTILE", field = "Lnet/minecraft/tags/DamageTypeTags;IS_PROJECTILE:Lnet/minecraft/tags/TagKey;")
    @Expression("?.is(IS_PROJECTILE)")
    @WrapOperation(method = "getDamageProtection", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacztweaks$getDamageProtection$bulletProtection(DamageSource instance, TagKey<DamageType> damageTypeKey, Operation<Boolean> original) {
        return original.call(instance, damageTypeKey)
            || (Config.Gameplay.Behaviour.bulletProtection() && instance.is(ModDamageTypes.BULLETS_TAG));
    }
}
//?} else {
/*import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public abstract class ProtectionEnchantmentMixin { }
*///?}