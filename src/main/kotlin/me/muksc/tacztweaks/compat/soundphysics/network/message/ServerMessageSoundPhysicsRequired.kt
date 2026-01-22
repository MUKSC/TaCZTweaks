package me.muksc.tacztweaks.compat.soundphysics.network.message

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsCompat
import me.muksc.tacztweaks.network.CustomPacketPayload
import me.muksc.tacztweaks.network.StreamCodec
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft

object ServerMessageSoundPhysicsRequired : CustomPacketPayload {
    val TYPE = CustomPacketPayload.Type<ServerMessageSoundPhysicsRequired>(
        TaCZTweaks.id("server_sound_physics_required")
    )
    val STREAM_CODEC = StreamCodec.Companion.of(
        encoder = { packet, buf -> /* Nothing */ },
        decoder = { buf -> ServerMessageSoundPhysicsRequired }
    )

    fun handle(packet: ServerMessageSoundPhysicsRequired, minecraft: Minecraft) {
        if (SoundPhysicsCompat.isEnabled()) return
        val message = TaCZTweaks.message()
            .append(TaCZTweaks.translatable("bullet_sounds.sound_physics_missing").withStyle(ChatFormatting.YELLOW))
        minecraft.gui.chat.addMessage(message)
    }

    override fun type(): CustomPacketPayload.Type<ServerMessageSoundPhysicsRequired> = TYPE
}