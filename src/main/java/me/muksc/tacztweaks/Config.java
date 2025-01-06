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
    private static final ForgeConfigSpec.BooleanValue RELOAD_WHILE_SHOOTING = BUILDER.define("reloadWhileShooting", true);
    private static final ForgeConfigSpec.ConfigValue<Double> CRAWL_PITCH_UPPER_LIMIT = BUILDER.comment("Default: 25.0").defineInRange("crawlPitchUpperLimit", 25.0F, 0F, 90F);
    private static final ForgeConfigSpec.ConfigValue<Double> CRAWL_PITCH_LOWER_LIMIT = BUILDER.comment("Default: -10.0").defineInRange("crawlPitchLowerLimit", -10.0, -90F, 0F);
    private static final ForgeConfigSpec.BooleanValue DYNAMIC_CRAWL_PITCH_LIMIT = BUILDER.define("dynamicCrawlPitchLimit", false);

    private static final ForgeConfigSpec.BooleanValue FIRST_AID_COMPAT = BUILDER.pop().push("misc").define("firstAidCompat", true);
    private static final ForgeConfigSpec.BooleanValue CRAWL_VISUAL_TWEAK = BUILDER.define("crawlVisualTweak", true);
    private static final ForgeConfigSpec.BooleanValue DISABLE_TACZ_CRAWL = BUILDER.comment("For better compatibility").define("disableTaCZCrawl", false);

    static final ForgeConfigSpec SPEC = BUILDER.pop().build();

    public static EShootWhileSprinting shootWhileSprinting;
    public static boolean sprintWhileReloading;
    public static boolean reloadWhileShooting;
    public static float crawlPitchUpperLimit;
    public static float crawlPitchLowerLimit;
    public static boolean dynamicCrawlPitchLimit;

    public static boolean firstAidCompat;
    public static boolean crawlVisualTweak;
    public static boolean disableTaCZCrawl;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        shootWhileSprinting = SHOOT_WHILE_SPRINTING.get();
        sprintWhileReloading = SPRINT_WHILE_RELOADING.get();
        reloadWhileShooting = RELOAD_WHILE_SHOOTING.get();
        crawlPitchUpperLimit = CRAWL_PITCH_UPPER_LIMIT.get().floatValue();
        crawlPitchLowerLimit = CRAWL_PITCH_LOWER_LIMIT.get().floatValue();
        dynamicCrawlPitchLimit = DYNAMIC_CRAWL_PITCH_LIMIT.get();

        firstAidCompat = FIRST_AID_COMPAT.get();
        crawlVisualTweak = CRAWL_VISUAL_TWEAK.get();
        disableTaCZCrawl = DISABLE_TACZ_CRAWL.get();
    }

    public enum EShootWhileSprinting {
        ALLOW,
        STOP_SPRINTING,
        DISABLED
    }
}
