package me.muksc.tacztweaks;

import com.tacz.guns.entity.EntityKineticBullet;
import me.muksc.tacztweaks.client.input.UnloadKey;
import me.muksc.tacztweaks.data.BulletInteractionManager;
import me.muksc.tacztweaks.data.BulletSoundsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TaCZTweaks.MOD_ID)
public class TaCZTweaks {
    public static final String MOD_ID = "tacztweaks";

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public TaCZTweaks(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerReloadListeners(AddReloadListenerEvent e) {
        e.addListener(BulletInteractionManager.INSTANCE);
    }

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent e) {
        if (!(e.level instanceof ServerLevel level)) return;
        BlockBreakingManager.INSTANCE.onLevelTick(level);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if (!(e.getLevel() instanceof ServerLevel level)) return;
        BlockBreakingManager.INSTANCE.onBlockBreak(level, e.getPos());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    static class ClientEvents {
        @SubscribeEvent
        public static void onRenderLevel(RenderLevelStageEvent event) {
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
            float partialTicks = event.getPartialTick();
            ClientLevel level = Minecraft.getInstance().level;
            LocalPlayer player = Minecraft.getInstance().player;
            if (level == null || player == null) return;
            for (Entity entity : level.entitiesForRendering()) {
                if (!(entity instanceof EntityKineticBullet bullet)) continue;
                BulletSoundsManager.INSTANCE.handleSoundWhizz(level, player, bullet, partialTicks);
            }
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(RegisterKeyMappingsEvent event) {
            event.register(UnloadKey.UNLOAD_KEY);
        }

        @SubscribeEvent
        public static void registerClientReloadListeners(RegisterClientReloadListenersEvent e) {
            e.registerReloadListener(BulletSoundsManager.INSTANCE);
        }
    }
}
