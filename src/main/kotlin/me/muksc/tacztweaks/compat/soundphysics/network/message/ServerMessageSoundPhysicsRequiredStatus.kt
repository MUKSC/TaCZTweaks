package me.muksc.tacztweaks.compat.soundphysics.network.message

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.network.CustomPacketPayload
import me.muksc.tacztweaks.network.StreamCodec
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft

object ServerMessageSoundPhysicsRequiredStatus : CustomPacketPayload {
    val TYPE = CustomPacketPayload.Type<ServerMessageSoundPhysicsRequiredStatus>(
        TaCZTweaks.id("server_sound_physics_required")
    )
    val STREAM_CODEC = StreamCodec.Companion.of(
        encoder = { packet, buf -> /* Nothing */ },
        decoder = { buf -> ServerMessageSoundPhysicsRequiredStatus }
    )

    fun handle(packet: ServerMessageSoundPhysicsRequiredStatus, minecraft: Minecraft) {
        val message = TaCZTweaks.message()
            .append(TaCZTweaks.translatable("bullet_sounds.sound_physics_missing").withStyle(ChatFormatting.YELLOW))
        minecraft.gui.chat.addMessage(message)
    }

    override fun type(): CustomPacketPayload.Type<ServerMessageSoundPhysicsRequiredStatus> = TYPE
}