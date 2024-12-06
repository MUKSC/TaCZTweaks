package me.muksc.tacztweaks;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = TaCZTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.EnumValue<EShootWhileSprinting> SHOOT_WHILE_SPRINTING = BUILDER.push("movement").defineEnum("shootWhileSprinting", EShootWhileSprinting.STOP_SPRINTING);
    private static final ForgeConfigSpec.BooleanValue SPRINT_WHILE_RELOADING = BUILDER.define("sprintWhileReloading", true);
    private static final ForgeConfigSpec.ConfigValue<Double> CRAWL_PITCH_UPPER_LIMIT = BUILDER.defineInRange("crawlPitchUpperLimit", 25.0F, 0F, 90F);
    private static final ForgeConfigSpec.ConfigValue<Double> CRAWL_PITCH_LOWER_LIMIT = BUILDER.defineInRange("crawlPitchLowerLimit", -10.0, -90F, 0F);

    private static final ForgeConfigSpec.BooleanValue PIERCE_BLOCKS = BUILDER.pop().push("bullet").define("pierceBlocks", true);
    private static final ForgeConfigSpec.ConfigValue<Float> PIERCE_DAMAGE_FALLOFF = BUILDER.define("pierceDamageFalloff", 5F);

    private static final ForgeConfigSpec.BooleanValue CRAWL_VISUAL_TWEAK = BUILDER.pop().push("misc").define("crawlVisualTweak", true);

    static final ForgeConfigSpec SPEC = BUILDER.pop().build();

    public static EShootWhileSprinting shootWhileSprinting;
    public static boolean sprintWhileReloading;
    public static float crawlPitchUpperLimit;
    public static float crawlPitchLowerLimit;

    public static boolean pierceBlocks;
    public static float pierceDamageFalloff;

    public static boolean crawlVisualTweak;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        shootWhileSprinting = SHOOT_WHILE_SPRINTING.get();
        sprintWhileReloading = SPRINT_WHILE_RELOADING.get();
        crawlPitchUpperLimit = CRAWL_PITCH_UPPER_LIMIT.get().floatValue();
        crawlPitchLowerLimit = CRAWL_PITCH_LOWER_LIMIT.get().floatValue();

        pierceBlocks = PIERCE_BLOCKS.get();
        pierceDamageFalloff = PIERCE_DAMAGE_FALLOFF.get();

        crawlVisualTweak = CRAWL_VISUAL_TWEAK.get();
    }

    public enum EShootWhileSprinting {
        ALLOW,
        STOP_SPRINTING,
        DISABLED
    }
}
