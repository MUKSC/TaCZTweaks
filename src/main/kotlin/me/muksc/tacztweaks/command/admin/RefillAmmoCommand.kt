package me.muksc.tacztweaks.command.admin

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.tacz.guns.api.item.IGun
import com.tacz.guns.util.AttachmentDataUtils
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.command.BaseCommand
import me.muksc.tacztweaks.core.tacz.GunStack
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import java.util.function.Supplier

object RefillAmmoCommand : BaseCommand("refill_ammo") {
    override fun build(builder: LiteralArgumentBuilder<CommandSourceStack>): LiteralArgumentBuilder<CommandSourceStack> =
        builder.requires { context ->
            context.hasPermission(2)
        }.executes { context ->
            execute(listOf(context.source.playerOrException), context.source::sendSuccess)
        }.then(Commands.argument("targets", EntityArgument.players()).executes { context ->
            val players = EntityArgument.getPlayers(context, "targets")
            execute(players, context.source::sendSuccess)
        })

    fun execute(players: Collection<ServerPlayer>, sendSuccess: (messageSupplier: Supplier<Component>, allowLogging: Boolean) -> Unit): Int {
        for (player in players) {
            for (stack in player.inventory.items) {
                val gun = stack.item as? IGun ?: continue
                val maxAmmoCount = GunStack(stack).index?.gunData?.let { gunData ->
                    AttachmentDataUtils.getAmmoCountWithAttachment(stack, gunData)
                }
                gun.setBulletInBarrel(stack, true)
                if (maxAmmoCount != null) gun.setCurrentAmmoCount(stack, maxAmmoCount)
            }
        }

        sendSuccess({
            if (players.size == 1) {
                TaCZTweaks.translatable("commands.$name.success.single", players.iterator().next().displayName)
            } else {
                TaCZTweaks.translatable("commands.$name.success.multiple", players.size)
            }
        }, true)
        return players.size
    }
}