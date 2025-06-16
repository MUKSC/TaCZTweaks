package me.muksc.tacztweaks.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.network.NetworkHandler;
import me.muksc.tacztweaks.network.message.ClientMessagePlayerUnload;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class UnloadKey {
    public static final KeyMapping UNLOAD_KEY = new KeyMapping(
        "key.tacztweaks.unload",
        KeyConflictContext.IN_GAME,
        KeyModifier.CONTROL,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "key.category.tacz"
    );

    @SubscribeEvent
    public static void onUnloadPress(InputEvent.Key event) {
        if (!isInGame() || !UNLOAD_KEY.consumeClick() || !Config.Gun.INSTANCE.allowUnload()) return;
        NetworkHandler.INSTANCE.sendC2S(ClientMessagePlayerUnload.INSTANCE);
    }
}