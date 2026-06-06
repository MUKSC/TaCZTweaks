package me.muksc.tacztweaks.mixin.feature.core.attribute;

//? if fabric {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.muksc.tacztweaks.effect.FlagStatusEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @ModifyExpressionValue(method = "removeAllEffects", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    private Collection<MobEffectInstance> tacztweaks$removeAllEffects$filter(Collection<MobEffectInstance> original) {
        //~ if >=1.21 '.getEffect()' -> '.getEffect().value()'
        return original.stream().filter(x -> x.getEffect() instanceof FlagStatusEffect).toList();
    }
}
*///?} else {
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin { }
//?}