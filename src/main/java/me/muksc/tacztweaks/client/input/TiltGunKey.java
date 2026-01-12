package me.muksc.tacztweaks.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.animation.statemachine.GunAnimationStateContext;
import com.tacz.guns.client.resource.GunDisplayInstance;
import me.muksc.tacztweaks.TaCZTweaks;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class TiltGunKey {
    public static KeyMapping KEY = new KeyMapping(
        TaCZTweaks.translatable("key.tiltGun").getString(),
        KeyConflictContext.IN_GAME,
        KeyModifier.NONE,
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        "key.category.tacz"
    );

    public static boolean isActive(LocalPlayer player) {
        if (!KEY.isDown() || !IGun.mainHandHoldGun(player)) return false;
        GunDisplayInstance display = TimelessAPI.getGunDisplay(player.getMainHandItem()).orElse(null);
        if (display == null) return false;
        GunAnimationStateContext context = display.getAnimationStateMachine().getContext();
        if (context == null) return false;
        return context.shouldSlide();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (!Config.Gun.INSTANCE.tiltGunKeyCancelsSprint()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !isActive(player)) return;
        player.setSprinting(false);
    }
}
