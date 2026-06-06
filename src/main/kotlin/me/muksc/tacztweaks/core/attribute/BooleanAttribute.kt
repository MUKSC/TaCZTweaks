package me.muksc.tacztweaks.core.attribute

import net.minecraft.core.Holder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute

//? if forge || fabric {
import me.muksc.tacztweaks.TaCZTweaks
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.ai.attributes.AttributeModifier

class BooleanAttribute(
    descriptionId: String,
    private val defaultValue: () -> Boolean
) : Attribute(descriptionId, if (defaultValue()) 1.0 else 0.0) {
    constructor(descriptionId: String, defaultValue: Boolean) : this(descriptionId, { defaultValue })

    override fun sanitizeValue(value: Double): Double {
        if (value.isNaN()) return 0.0
        return if (value > 0.0) 1.0 else 0.0
    }

    override fun getDefaultValue(): Double = if (defaultValue()) 1.0 else 0.0

    //? if 1.20.1 {
    fun toValueComponent(op: AttributeModifier.Operation?, value: Double): MutableComponent = when (op) {
        null -> TaCZTweaks.translatable("attribute.value.boolean.${if (value > 0) "enabled" else "disabled"}")
        AttributeModifier.Operation.ADDITION if value > 0 -> TaCZTweaks.translatable("attribute.value.boolean.enable")
        AttributeModifier.Operation.MULTIPLY_TOTAL if value == -1.0 -> TaCZTweaks.translatable("attribute.value.boolean.disable")
        else -> TaCZTweaks.translatable("attribute.value.boolean.invalid")
    }
    //?} else if 1.21.1 {
    /*fun toValueComponent(op: AttributeModifier.Operation?, value: Double): MutableComponent = when (op) {
        null -> TaCZTweaks.translatable("attribute.value.boolean.${if (value > 0) "enabled" else "disabled"}")
        AttributeModifier.Operation.ADD_VALUE if value > 0 -> TaCZTweaks.translatable("attribute.value.boolean.enable")
        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL if value == -1.0 -> TaCZTweaks.translatable("attribute.value.boolean.disable")
        else -> TaCZTweaks.translatable("attribute.value.boolean.invalid")
    }
    *///?}

    fun toComponent(modifier: AttributeModifier): MutableComponent {
        val value = modifier.amount
        val color = if (value > 0.0) {
            ChatFormatting.BLUE
        } else {
            ChatFormatting.RED
        }
        return TaCZTweaks.translatable(
            "attribute.modifier.bool",
            toValueComponent(modifier.operation, value),
            Component.translatable(descriptionId)
        ).withStyle(color)
    }
}
//?} else if neoforge {
/*import net.neoforged.neoforge.common.BooleanAttribute as NeoBooleanAttribute

class BooleanAttribute(
    descriptionId: String,
    private val defaultValue: () -> Boolean
) : NeoBooleanAttribute(descriptionId, defaultValue()) {
    constructor(descriptionId: String, defaultValue: Boolean) : this(descriptionId, { defaultValue })

    override fun getDefaultValue(): Double = if (defaultValue()) 1.0 else 0.0
}
*///?}

fun LivingEntity.getBooleanAttributeValue(attribute: Holder<Attribute>): Boolean = getAttributeValue(attribute) > 0

//? if <1.21
fun LivingEntity.getBooleanAttributeValue(attribute: Attribute): Boolean = getAttributeValue(attribute) > 0