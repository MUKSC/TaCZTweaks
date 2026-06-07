package me.muksc.tacztweaks.mixin.feature.general.compatibility.firstaid;

//? if 1.20.1 && forge {
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.init.ModDamageTypes;
import ichttt.mods.firstaid.common.EventHandler;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EventHandler.class, remap = false)
public abstract class EventHandlerMixin {
    @Definition(id = "is", method = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z", remap = true)
    @Definition(id = "IS_PROJECTILE", field = "Lnet/minecraft/tags/DamageTypeTags;IS_PROJECTILE:Lnet/minecraft/tags/TagKey;", remap = true)
    @Expression("?.is(IS_PROJECTILE)")
    @WrapOperation(method = "onLivingHurt", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean tacztweaks$onLivingHurt$firstAidCompat$markAsProjectile(DamageSource instance, TagKey<DamageType> damageTypeKey, Operation<Boolean> original) {
        return original.call(instance, damageTypeKey)
            || (Config.General.Compatibility.firstAidCompat() && instance.is(ModDamageTypes.BULLETS_TAG));
    }
}
//?} else {
/*import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public abstract class EventHandlerMixin { }
*///?}