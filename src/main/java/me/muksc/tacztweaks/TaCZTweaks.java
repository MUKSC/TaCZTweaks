package me.muksc.tacztweaks;

import me.muksc.tacztweaks.client.input.UnloadKey;
import me.muksc.tacztweaks.data.BulletInteractionManager;
import me.muksc.tacztweaks.data.BulletSoundsManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

@Mod(TaCZTweaks.MOD_ID)
public class TaCZTweaks {
    public static final String MOD_ID = "tacztweaks";

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public final FMLModContainer container;

    public TaCZTweaks(FMLJavaModLoadingContext context) {
        container = context.getContainer();
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerReloadListeners(AddReloadListenerEvent e) {
        e.addListener(BulletInteractionManager.INSTANCE);
        e.addListener(BulletSoundsManager.INSTANCE);
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

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (!BulletInteractionManager.INSTANCE.getError()) return;
        MutableComponent text = ComponentUtils.wrapInSquareBrackets(Component.literal(container.getModInfo().getDisplayName()))
            .append(Component.literal(" "))
            .append(Component.translatable("tacztweaks.bullet_interactions.error").withStyle(ChatFormatting.RED));
        e.getEntity().sendSystemMessage(text);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(RegisterKeyMappingsEvent event) {
            event.register(UnloadKey.UNLOAD_KEY);
        }
    }
}
