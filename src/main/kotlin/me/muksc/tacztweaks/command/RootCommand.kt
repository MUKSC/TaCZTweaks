package me.muksc.tacztweaks.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.command.admin.RefillAmmoCommand
import net.minecraft.commands.CommandSourceStack

object RootCommand : BaseCommand(TaCZTweaks.MOD_ID) {
    val subcommands = arrayOf(
        RefillAmmoCommand
    )

    override fun build(builder: LiteralArgumentBuilder<CommandSourceStack>): LiteralArgumentBuilder<CommandSourceStack> = builder.apply {
        for (subcommand in subcommands) {
            builder.then(subcommand.build())
        }
    }
}