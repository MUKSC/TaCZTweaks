package me.muksc.tacztweaks.mixin.feature.general.compatibility.apothic_attributes;

//? if forge || neoforge {
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.entity.EntityKineticBullet;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.mixin.accessor.EntityKineticBulletAccessor;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//~ if <1.20.5 'apothic_attributes' -> 'attributeslib'
import dev.shadowsoffire.attributeslib.api.ALCombatRules;

@Mixin(value = ALCombatRules.class, remap = false)
public abstract class ALCombatRulesMixin {
    //? if <1.20.5 {
    @Definition(id = "getAttributeValue", method = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D")
    @Definition(id = "ARMOR_PIERCE", field = "Ldev/shadowsoffire/attributeslib/api/ALObjects$Attributes;ARMOR_PIERCE:Lnet/minecraftforge/registries/RegistryObject;")
    @Definition(id = "get", method = "Lnet/minecraftforge/registries/RegistryObject;get()Ljava/lang/Object;")
    @Expression("(float) ?.getAttributeValue((?) ARMOR_PIERCE.get())")
    //?} else {
    /*@Definition(id = "getAttributeValue", method = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D")
    @Definition(id = "ARMOR_PIERCE", field = "Ldev/shadowsoffire/apothic_attributes/api/ALObjects$Attributes;ARMOR_PIERCE:Lnet/minecraft/core/Holder;")
    @Expression("(float) ?.getAttributeValue(ARMOR_PIERCE)")
    *///?}
    @ModifyExpressionValue(method = "getDamageAfterArmor", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static float tacztweaks$getDamageAfterArmor$bulletArmorIgnore(
        float original,
        @Local(argsOnly = true) DamageSource src
    ) {
        if (!Config.General.Compatibility.apothicAttributesCompat()) return original;
        if (!(src.getDirectEntity() instanceof EntityKineticBullet bullet)) return original;
        EntityKineticBulletAccessor accessor = (EntityKineticBulletAccessor) bullet;
        return original * accessor.tacztweaks$getArmorIgnore();
    }
}
//?} else {
/*import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public abstract class ALCombatRulesMixin { }
*///?}