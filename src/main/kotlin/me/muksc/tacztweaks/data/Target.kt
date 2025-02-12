package me.muksc.tacztweaks.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable

class Target(
    val type: EType,
    val values: List<ResourceLocation>
) {
    enum class EType : StringRepresentable {
        GUN,
        AMMO;

        override fun getSerializedName(): String = name.lowercase()

        companion object {
            val CODEC = StringRepresentable.fromEnum(::values)
        }
    }

    companion object {
        val DEFAULT = Target(EType.GUN, emptyList())
        val CODEC = RecordCodecBuilder.create<Target> { it.group(
            EType.CODEC.fieldOf("type").forGetter(Target::type),
            Codec.list(ResourceLocation.CODEC).fieldOf("values").forGetter(Target::values)
        ).apply(it, ::Target) }
    }
}