package me.muksc.tacztweaks;

import me.muksc.tacztweaks.client.input.UnloadKey;
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsCompat;
import me.muksc.tacztweaks.data.BulletInteractionManager;
import me.muksc.tacztweaks.data.BulletParticlesManager;
import me.muksc.tacztweaks.data.BulletSoundsManager;
import me.muksc.tacztweaks.network.NetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(TaCZTweaks.MOD_ID)
public class TaCZTweaks {
    public static final String MOD_ID = "tacztweaks";

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static MutableComponent translatable(String key, Object... args) {
        return Component.translatable("%s.%s".formatted(MOD_ID, key), args);
    }

    public final ModContainer container;

    public TaCZTweaks() {
        container = ModLoadingContext.get().getActiveContainer();
        Config.INSTANCE.touch();
        NetworkHandler.INSTANCE.register();
        SoundPhysicsCompat.INSTANCE.initialize();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerReloadListeners(AddReloadListenerEvent e) {
        e.addListener(BulletInteractionManager.INSTANCE);
        e.addListener(BulletParticlesManager.INSTANCE);
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
        if (BulletInteractionManager.INSTANCE.hasError()) {
            MutableComponent text = ComponentUtils.wrapInSquareBrackets(Component.literal(container.getModInfo().getDisplayName()))
                .append(Component.literal(" "))
                .append(TaCZTweaks.translatable("bullet_interactions.error").withStyle(ChatFormatting.RED));
            e.getEntity().sendSystemMessage(text);
        }
        if (BulletParticlesManager.INSTANCE.hasError()) {
            MutableComponent text = ComponentUtils.wrapInSquareBrackets(Component.literal(container.getModInfo().getDisplayName()))
                .append(Component.literal(" "))
                .append(TaCZTweaks.translatable("bullet_particles.error").withStyle(ChatFormatting.RED));
            e.getEntity().sendSystemMessage(text);
        }
        if (BulletSoundsManager.INSTANCE.hasError()) {
            MutableComponent text = ComponentUtils.wrapInSquareBrackets(Component.literal(container.getModInfo().getDisplayName()))
                .append(Component.literal(" "))
                .append(TaCZTweaks.translatable("bullet_sounds.error").withStyle(ChatFormatting.RED));
            e.getEntity().sendSystemMessage(text);
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MOD_ID, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, screen) -> Config.INSTANCE.generateConfigScreen(screen))
            );
        }

        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(UnloadKey.UNLOAD_KEY);
        }
    }
}
