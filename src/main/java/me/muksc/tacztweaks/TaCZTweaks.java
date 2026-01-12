package me.muksc.tacztweaks;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import me.muksc.tacztweaks.client.input.ReduceSensitivityKey;
import me.muksc.tacztweaks.client.input.TiltGunKey;
import me.muksc.tacztweaks.client.input.UnloadKey;
import me.muksc.tacztweaks.compat.lrtactical.LRTacticalCompat;
import me.muksc.tacztweaks.compat.pillagers_gun.PillagersGunCompat;
import me.muksc.tacztweaks.compat.soundphysics.SoundPhysicsCompat;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.core.BlockBreakingManager;
import me.muksc.tacztweaks.data.manager.*;
import me.muksc.tacztweaks.mixin.accessor.InaccuracyTypeAccessor;
import me.muksc.tacztweaks.mixininterface.gun.SlideDataHolder;
import me.muksc.tacztweaks.network.NetworkHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mod(TaCZTweaks.MOD_ID)
public class TaCZTweaks {
    public static final String MOD_ID = "tacztweaks";

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static MutableComponent translatable(String key, Object... args) {
        return Component.translatable("%s.%s".formatted(MOD_ID, key), args);
    }

    public static MutableComponent message() {
        return ComponentUtils.wrapInSquareBrackets(Component.literal(container.getModInfo().getDisplayName())).append(" ");
    }

    public static ModContainer container;

    public static List<BaseDataManager<?>> managers = Collections.emptyList();

    public TaCZTweaks() {
        container = ModLoadingContext.get().getActiveContainer();
        managers = List.of(
            BulletInteractionManager.INSTANCE,
            BulletParticlesManager.INSTANCE,
            BulletSoundsManager.INSTANCE,
            MeleeInteractionManager.INSTANCE
        );
        Config.INSTANCE.touch();
        NetworkHandler.INSTANCE.register();
        LRTacticalCompat.INSTANCE.initialize();
        PillagersGunCompat.INSTANCE.initialize();
        SoundPhysicsCompat.INSTANCE.initialize();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static List<InaccuracyType> getInaccuracyTypes(LivingEntity entity) {
        IGunOperator operator = IGunOperator.fromLivingEntity(entity);
        List<InaccuracyType> list = new ArrayList<>();
        if (InaccuracyTypeAccessor.isMove(entity)) list.add(InaccuracyType.MOVE);
        if (entity.getPose() == Pose.CROUCHING) list.add(InaccuracyType.SNEAK);
        if (entity.getPose() == Pose.SWIMMING && !entity.isSwimming()) list.add(InaccuracyType.LIE);
        if (operator.getSynAimingProgress() >= 1.0F) list.add(InaccuracyType.AIM);
        return list;
    }

    public static float getBetterInaccuracy(Map<InaccuracyType, Float> map, LivingEntity entity) {
        List<InaccuracyType> inaccuracyTypes = getInaccuracyTypes(entity);
        float base = map.get(InaccuracyType.STAND);
        float inaccuracy = base;
        if (inaccuracyTypes.contains(InaccuracyType.MOVE)) inaccuracy *= map.get(InaccuracyType.MOVE) / base;
        if (inaccuracyTypes.contains(InaccuracyType.SNEAK)) inaccuracy *= map.get(InaccuracyType.SNEAK) / base;
        if (inaccuracyTypes.contains(InaccuracyType.LIE)) inaccuracy *= map.get(InaccuracyType.LIE) / base;
        if (inaccuracyTypes.contains(InaccuracyType.AIM)) inaccuracy *= map.get(InaccuracyType.AIM) / base;
        if (Config.Tweaks.INSTANCE.betterGunTilt() && ((SlideDataHolder) entity).tacztweaks$getShouldSlide()) inaccuracy *= map.get(InaccuracyType.SNEAK) / base;
        return inaccuracy;
    }

    @SubscribeEvent
    public void onGunShoot(GunShootEvent e) {
        if (!Config.Gun.INSTANCE.disableUnderwater()) return;
        if (e.getShooter().isUnderWater()) e.setCanceled(true);
    }

    @SubscribeEvent
    public void registerReloadListeners(AddReloadListenerEvent e) {
        managers.forEach(e::addListener);
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer player)) return;
        managers.forEach(manager -> manager.notifyPlayer(player));
    }

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (!(e.level instanceof ServerLevel level)) return;
        BlockBreakingManager.INSTANCE.onLevelTick(level);
        BulletParticlesManager.INSTANCE.onLevelTick(level);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if (!(e.getLevel() instanceof ServerLevel level)) return;
        BlockBreakingManager.INSTANCE.onBlockBreak(level, e.getPos());
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
            event.register(ReduceSensitivityKey.KEY);
            event.register(TiltGunKey.KEY);
            event.register(UnloadKey.KEY);
        }
    }
}
