package me.muksc.tacztweaks.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.TaCZTweaks;
import me.muksc.tacztweaks.network.NetworkHandler;
import me.muksc.tacztweaks.network.message.ClientMessagePlayerUnload;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class UnloadKey {
    public static final KeyMapping KEY = new KeyMapping(
        TaCZTweaks.translatable("key.unload").getString(),
        KeyConflictContext.IN_GAME,
        KeyModifier.CONTROL,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "key.category.tacz"
    );

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (!Config.Gun.INSTANCE.allowUnload()) return;
        while (KEY.consumeClick()) {
            NetworkHandler.INSTANCE.sendC2S(ClientMessagePlayerUnload.INSTANCE);
        }
    }
}