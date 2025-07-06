package me.muksc.tacztweaks.mixin.modifiers;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.util.AttachmentDataUtils;
import me.muksc.tacztweaks.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AttachmentDataUtils.class, remap = false)
public abstract class AttachmentDataUtilsMixin {
    @Definition(id = "DAMAGE_BASE_MULTIPLIER", field = "Lcom/tacz/guns/config/sync/SyncConfig;DAMAGE_BASE_MULTIPLIER:Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;")
    @Definition(id = "get", method = "Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;get()Ljava/lang/Object;")
    @Definition(id = "Double", type = Double.class)
    @Expression("? * (Double) DAMAGE_BASE_MULTIPLIER.get()")
    @ModifyExpressionValue(method = "getDamageWithAttachment", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static double tacztweaks$getDamageWithAttachment$damageModifier(double original) {
        return AttachmentPropertyManager.eval(Config.Modifiers.Damage.INSTANCE.toTaCZ(), original);
    }

    @Definition(id = "HEAD_SHOT_BASE_MULTIPLIER", field = "Lcom/tacz/guns/config/sync/SyncConfig;HEAD_SHOT_BASE_MULTIPLIER:Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;")
    @Definition(id = "get", method = "Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;get()Ljava/lang/Object;")
    @Definition(id = "Double", type = Double.class)
    @Expression("? * (Double) HEAD_SHOT_BASE_MULTIPLIER.get()")
    @ModifyExpressionValue(method = "getHeadshotMultiplier", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static double tacztweaks$getHeadshotMultiplier$headshotModifier(double original) {
        return AttachmentPropertyManager.eval(Config.Modifiers.Headshot.INSTANCE.toTaCZ(), original);
    }

    @Definition(id = "ARMOR_IGNORE_BASE_MULTIPLIER", field = "Lcom/tacz/guns/config/sync/SyncConfig;ARMOR_IGNORE_BASE_MULTIPLIER:Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;")
    @Definition(id = "get", method = "Lnet/minecraftforge/common/ForgeConfigSpec$DoubleValue;get()Ljava/lang/Object;")
    @Definition(id = "Double", type = Double.class)
    @Expression("? * (Double) ARMOR_IGNORE_BASE_MULTIPLIER.get()")
    @ModifyExpressionValue(method = "getArmorIgnoreWithAttachment", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static double tacztweaks$getArmorIgnoreWithAttachment$armorIgnoreModifier(double original) {
        return AttachmentPropertyManager.eval(Config.Modifiers.ArmorIgnore.INSTANCE.toTaCZ(), original);
    }
}
