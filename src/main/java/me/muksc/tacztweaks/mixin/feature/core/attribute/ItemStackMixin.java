package me.muksc.tacztweaks.mixin.feature.core.attribute;

//? if forge || fabric {
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.muksc.tacztweaks.core.attribute.BooleanAttribute;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if <1.20.5 {
import java.util.Map;
//?} else {
/*import net.minecraft.core.Holder;
*///?}

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    //? if <1.20.5 {
    @Definition(id = "translatable", method = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;")
    @Expression("translatable('attribute.modifier.plus.' + ?, ?)")
    @WrapOperation(method = "getTooltipLines", at = @At("MIXINEXTRAS:EXPRESSION"))
    private MutableComponent tacztweaks$getTooltipLines$modifyTooltip$1(
        String key, Object[] args, Operation<MutableComponent> original,
        @Local Map.Entry<Attribute, AttributeModifier> entry
    ) {
        if (!(entry.getKey() instanceof BooleanAttribute attribute)) return original.call(key, args);
        AttributeModifier modifier = entry.getValue();
        return attribute.toComponent(modifier);
    }

    @Definition(id = "translatable", method = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;")
    @Expression("translatable('attribute.modifier.take.' + ?, ?)")
    @WrapOperation(method = "getTooltipLines", at = @At("MIXINEXTRAS:EXPRESSION"))
    private MutableComponent tacztweaks$getTooltipLines$modifyTooltip$2(
        String key, Object[] args, Operation<MutableComponent> original,
        @Local Map.Entry<Attribute, AttributeModifier> entry
    ) {
        if (!(entry.getKey() instanceof BooleanAttribute attribute)) return original.call(key, args);
        AttributeModifier modifier = entry.getValue();
        return attribute.toComponent(modifier);
    }
    //?} else {
    /*@Definition(id = "translatable", method = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;")
    @Expression("translatable('attribute.modifier.plus.' + ?, ?)")
    @WrapOperation(method = "addModifierTooltip", at = @At("MIXINEXTRAS:EXPRESSION"))
    private MutableComponent tacztweaks$addModifierTooltip$modifyTooltip$1(
        String key, Object[] args, Operation<MutableComponent> original,
        @Local(argsOnly = true) Holder<Attribute> attribute,
        @Local(argsOnly = true) AttributeModifier modifier
    ) {
        if (!(attribute.value() instanceof BooleanAttribute booleanAttribute)) return original.call(key, args);
        return booleanAttribute.toComponent(modifier);
    }

    @Definition(id = "translatable", method = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;")
    @Expression("translatable('attribute.modifier.take.' + ?, ?)")
    @WrapOperation(method = "addModifierTooltip", at = @At("MIXINEXTRAS:EXPRESSION"))
    private MutableComponent tacztweaks$addModifierTooltip$modifyTooltip$2(
        String key, Object[] args, Operation<MutableComponent> original,
        @Local(argsOnly = true) Holder<Attribute> attribute,
        @Local(argsOnly = true) AttributeModifier modifier
    ) {
        if (!(attribute.value() instanceof BooleanAttribute booleanAttribute)) return original.call(key, args);
        return booleanAttribute.toComponent(modifier);
    }
    *///?}
}
//?} else if neoforge {
/*import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin { }
*///?}