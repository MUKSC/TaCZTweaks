package me.muksc.tacztweaks;

import me.muksc.tacztweaks.client.input.UnloadKey;
import me.muksc.tacztweaks.data.BulletInteractionManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(TaCZTweaks.MOD_ID)
public class TaCZTweaks {
    public static final String MOD_ID = "tacztweaks";

    public ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @SuppressWarnings("removal")
    public TaCZTweaks() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
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

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID, value = Dist.CLIENT)
    static class ClientSetupEvent {
        @SubscribeEvent
        public static void onClientSetup(RegisterKeyMappingsEvent event) {
            event.register(UnloadKey.UNLOAD_KEY);
        }
    }
}
