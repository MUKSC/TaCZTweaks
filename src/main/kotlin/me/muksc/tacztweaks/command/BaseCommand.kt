package me.muksc.tacztweaks.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

abstract class BaseCommand(val name: String) {
    fun build(): LiteralArgumentBuilder<CommandSourceStack> = build(Commands.literal(name))

    abstract fun build(builder: LiteralArgumentBuilder<CommandSourceStack>): LiteralArgumentBuilder<CommandSourceStack>
}