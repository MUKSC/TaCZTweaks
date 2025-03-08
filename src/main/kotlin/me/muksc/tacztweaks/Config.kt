package me.muksc.tacztweaks

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.config.v3.CodecConfig
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig
import dev.isxander.yacl3.platform.YACLPlatform
import dev.isxander.yacl3.config.v3.register
import dev.isxander.yacl3.config.v3.value
import dev.isxander.yacl3.dsl.ControllerBuilderFactory
import dev.isxander.yacl3.dsl.slider
import net.minecraft.client.gui.screens.Screen

@Suppress("UnstableApiUsage")
object Config : JsonFileCodecConfig<Config>(
    YACLPlatform.getConfigDir().resolve("${TaCZTweaks.MOD_ID}.json")
) {
    val gun by register(Gun, Gun)
    val crawl by register(Crawl, Crawl)
    val compat by register(Compat, Compat)

    object Gun : CodecConfig<Gun>() {
        val shootWhileSprinting by register(true, BOOL)
        val sprintWhileReloading by register(true, BOOL)
        val reloadWhileShooting by register(true, BOOL)
        val allowUnload by register(true, BOOL)

        fun shootWhileSprinting(): Boolean = shootWhileSprinting.value
        fun sprintWhileReloading(): Boolean = sprintWhileReloading.value
        fun reloadWhileShooting(): Boolean = reloadWhileShooting.value
        fun allowUnload(): Boolean = allowUnload.value
    }

    object Crawl : CodecConfig<Crawl>() {
        val enabled by register(true, BOOL)
        val pitchUpperLimit by register(25.0F, FLOAT)
        val pitchLowerLimit by register(-10.0F, FLOAT)
        val dynamicPitchLimit by register(false, BOOL)
        val visualTweak by register(true, BOOL)

        fun enabled(): Boolean = enabled.value
        fun pitchUpperLimit(): Float = pitchUpperLimit.value
        fun pitchLowerLimit(): Float = pitchLowerLimit.value
        fun dynamicPitchLimit(): Boolean = dynamicPitchLimit.value
        fun visualTweak(): Boolean = visualTweak.value
    }

    object Compat : CodecConfig<Compat>() {
        val firstAidCompat by register(true, BOOL)
        val lsoCompat by register(true, BOOL)
        val vsCollisionCompat by register(false, BOOL)
        val vsExplosionCompat by register(false, BOOL)

        fun firstAidCompat(): Boolean = firstAidCompat.value
        fun lsoCompat(): Boolean = lsoCompat.value
        fun vsCollisionCompat(): Boolean = vsCollisionCompat.value
        fun vsExplosionCompat(): Boolean = vsExplosionCompat.value
    }

    fun generateConfigScreen(parent: Screen?): Screen = YetAnotherConfigLib.createBuilder().apply {
        title(TaCZTweaks.translatable("config.title"))
        save(::saveToFile)

        category(ConfigCategory.createBuilder().apply {
            name(TaCZTweaks.translatable("config.title"))
            group(OptionGroup.createBuilder().apply {
                name(TaCZTweaks.translatable("config.gun"))
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.gun.shootWhileSprinting.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.gun.shootWhileSprinting.description")))
                    binding(Gun.shootWhileSprinting.asBinding())
                    controller(booleanController())
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.gun.sprintWhileReloading.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.gun.sprintWhileReloading.description")))
                    binding(Gun.sprintWhileReloading.asBinding())
                    controller(booleanController())
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.gun.reloadWhileShooting.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.gun.reloadWhileShooting.description")))
                    binding(Gun.reloadWhileShooting.asBinding())
                    controller(booleanController())
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.gun.allowUnload.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.gun.allowUnload.description")))
                    binding(Gun.allowUnload.asBinding())
                    controller(booleanController())
                }.build())
            }.build())
            group(OptionGroup.createBuilder().apply {
                name(TaCZTweaks.translatable("config.crawl"))
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.crawl.enabled.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.crawl.enabled.description")))
                    binding(Crawl.enabled.asBinding())
                    controller(booleanController())
                }.build())
                option(Option.createBuilder<Float>().apply {
                    name(TaCZTweaks.translatable("config.crawl.pitchUpperLimit.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.crawl.pitchUpperLimit.description")))
                    binding(Crawl.pitchUpperLimit.asBinding())
                    controller(slider(range = 0.0F..90.0F, step = 1.0F) { TaCZTweaks.translatable("config.label.degree", it) })
                }.build())
                option(Option.createBuilder<Float>().apply {
                    name(TaCZTweaks.translatable("config.crawl.pitchLowerLimit.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.crawl.pitchLowerLimit.description")))
                    binding(Crawl.pitchLowerLimit.asBinding())
                    controller(slider(range = -90.0F..0.0F, step = 1.0F) { TaCZTweaks.translatable("config.label.degree", it) })
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.crawl.dynamicPitchLimit.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.crawl.dynamicPitchLimit.description")))
                    binding(Crawl.dynamicPitchLimit.asBinding())
                    controller(booleanController())
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.crawl.visualTweak.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.crawl.visualTweak.description")))
                    binding(Crawl.visualTweak.asBinding())
                    controller(booleanController())
                }.build())
            }.build())
            group(OptionGroup.createBuilder().apply {
                name(TaCZTweaks.translatable("config.compat"))
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.compat.firstAidCompat.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.compat.firstAidCompat.description")))
                    binding(Compat.firstAidCompat.asBinding())
                    controller(booleanController())
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.compat.lsoCompat.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.compat.lsoCompat.description")))
                    binding(Compat.lsoCompat.asBinding())
                    controller(booleanController())
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.compat.vsCollisionCompat.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.compat.vsCollisionCompat.description")))
                    binding(Compat.vsCollisionCompat.asBinding())
                    controller(booleanController())
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.compat.vsExplosionCompat.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.compat.vsExplosionCompat.description")))
                    binding(Compat.vsExplosionCompat.asBinding())
                    controller(booleanController())
                }.build())
            }.build())
        }.build())
    }.build().generateScreen(parent)

    private fun booleanController(): ControllerBuilderFactory<Boolean> = { option ->
        BooleanControllerBuilder.create(option)
            .formatValue { when (it) {
                true -> TaCZTweaks.translatable("config.label.enabled")
                false -> TaCZTweaks.translatable("config.label.disabled")
            } }
            .coloured(true)
    }

    fun touch() { /* Nothing */ }

    init {
        if (!loadFromFile()) {
            saveToFile()
        }
    }
}
