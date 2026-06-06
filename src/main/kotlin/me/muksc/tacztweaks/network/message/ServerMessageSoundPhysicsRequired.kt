package me.muksc.tacztweaks.network.message

import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.codec.StreamCodec
import me.muksc.tacztweaks.core.network.CustomPacketPayload
import me.muksc.tacztweaks.core.network.CustomPacketPayloadType
import me.muksc.tacztweaks.feature.sound_physics_evaluate.SoundPhysicsManager
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf

object ServerMessageSoundPhysicsRequired : CustomPacketPayload<ServerMessageSoundPhysicsRequired> {
    val TYPE = CustomPacketPayloadType<ServerMessageSoundPhysicsRequired>(
        TaCZTweaks.id("server_sound_physics_required")
    )
    val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ServerMessageSoundPhysicsRequired> = StreamCodec.unit(ServerMessageSoundPhysicsRequired)

    fun handle(packet: ServerMessageSoundPhysicsRequired, minecraft: Minecraft) = minecraft.execute {
        if (SoundPhysicsManager.loaded) return@execute
        minecraft.gui.chat.addMessage(TaCZTweaks.message().append(
            TaCZTweaks.translatable("datapack.bullet_sounds.sound_physics_missing")
                .withStyle(ChatFormatting.YELLOW)
        ))
    }

    override fun self(): ServerMessageSoundPhysicsRequired = this

    override fun type(): CustomPacketPayloadType<ServerMessageSoundPhysicsRequired> = TYPE

    override fun codec(): StreamCodec<FriendlyByteBuf, ServerMessageSoundPhysicsRequired> = STREAM_CODEC
}