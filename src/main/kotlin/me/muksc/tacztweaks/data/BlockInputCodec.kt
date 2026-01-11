package me.muksc.tacztweaks.data

import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.commands.arguments.blocks.BlockInput
import net.minecraft.commands.arguments.blocks.BlockStateParser
import net.minecraft.core.registries.BuiltInRegistries

val BlockInputCodec: Codec<BlockInput> = Codec.STRING.comapFlatMap({
    val result = try {
        @Suppress("DEPRECATION")
        BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), it, true)
    } catch (e: CommandSyntaxException) {
        return@comapFlatMap DataResult.error { e.message }
    }
    DataResult.success(BlockInput(result.blockState, result.properties.keys, result.nbt))
}, {
    val tag = it.tag?.takeIf { tag -> !tag.isEmpty }
    if (tag != null) return@comapFlatMap "${BlockStateParser.serialize(it.state)}${tag.asString}"
    BlockStateParser.serialize(it.state)
})