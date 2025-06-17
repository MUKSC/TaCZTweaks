package me.muksc.tacztweaks

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.config.v3.CodecConfig
import dev.isxander.yacl3.config.v3.register
import dev.isxander.yacl3.config.v3.value
import dev.isxander.yacl3.dsl.ControllerBuilderFactory
import dev.isxander.yacl3.dsl.slider
import dev.isxander.yacl3.platform.YACLPlatform
import me.muksc.tacztweaks.config.ConfigManager
import me.muksc.tacztweaks.config.ESyncDirection
import me.muksc.tacztweaks.config.SyncableCodecConfig
import me.muksc.tacztweaks.config.SyncableJsonFileCodecConfig
import me.muksc.tacztweaks.network.NetworkHandler
import me.muksc.tacztweaks.network.message.ClientMessageSyncConfig
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.screens.Screen

@Suppress("UnstableApiUsage")
object Config : SyncableJsonFileCodecConfig<Config>(
    YACLPlatform.getConfigDir().resolve("${TaCZTweaks.MOD_ID}.json")
) {
    val gun by registerSyncable(Gun)
    val crawl by registerSyncable(Crawl)
    val compat by registerSyncable(Compat)
    val tweaks by register(Tweaks, Tweaks)

    object Gun : SyncableCodecConfig<Gun>() {
        val shootWhileSprinting by registerSyncable(
            default = true,
            codec = BOOL,
            encoder = { buf, value -> buf.writeBoolean(value) },
            decoder = { buf -> buf.readBoolean() }
        )
        val sprintWhileReloading by registerSyncable(
            default = true,
            codec = BOOL,
            encoder = { buf, value -> buf.writeBoolean(value) },
            decoder = { buf -> buf.readBoolean() }
        )
        val reloadWhileShooting by registerSyncable(
            default = true,
            codec = BOOL,
            encoder = { buf, value -> buf.writeBoolean(value) },
            decoder = { buf -> buf.readBoolean() }
        )
        val allowUnload by registerSyncable(
            default = true,
            codec = BOOL,
            encoder = { buf, value -> buf.writeBoolean(value) },
            decoder = { buf -> buf.readBoolean() }
        )
        val disableBulletCulling by register(false, BOOL)

        fun shootWhileSprinting(): Boolean = shootWhileSprinting.syncedValue
        fun sprintWhileReloading(): Boolean = sprintWhileReloading.syncedValue
        fun reloadWhileShooting(): Boolean = reloadWhileShooting.syncedValue
        fun allowUnload(): Boolean = allowUnload.syncedValue
        fun disableBulletCulling(): Boolean = disableBulletCulling.value
    }

    object Crawl : SyncableCodecConfig<Crawl>() {
        val enabled by registerSyncable(
            default = true,
            codec = BOOL,
            encoder = { buf, value -> buf.writeBoolean(value) },
            decoder = { buf -> buf.readBoolean() }
        )
        val pitchUpperLimit by register(25.0F, FLOAT)
        val pitchLowerLimit by register(-10.0F, FLOAT)
        val dynamicPitchLimit by register(false, BOOL)
        val visualTweak by register(true, BOOL)

        fun enabled(): Boolean = enabled.syncedValue
        fun pitchUpperLimit(): Float = pitchUpperLimit.value
        fun pitchLowerLimit(): Float = pitchLowerLimit.value
        fun dynamicPitchLimit(): Boolean = dynamicPitchLimit.value
        fun visualTweak(): Boolean = visualTweak.value
    }

    object Compat : SyncableCodecConfig<Compat>() {
        val firstAidCompat by registerSyncable(
            default = true,
            codec = BOOL,
            encoder = { buf, value -> buf.writeBoolean(value) },
            decoder = { buf -> buf.readBoolean() }
        )
        val lsoCompat by registerSyncable(
            default = true,
            codec = BOOL,
            encoder = { buf, value -> buf.writeBoolean(value) },
            decoder = { buf -> buf.readBoolean() }
        )
        val vsCollisionCompat by registerSyncable(
            default = false,
            codec = BOOL,
            encoder = { buf, value -> buf.writeBoolean(value) },
            decoder = { buf -> buf.readBoolean() }
        )
        val vsExplosionCompat by registerSyncable(
            default = false,
            codec = BOOL,
            encoder = { buf, value -> buf.writeBoolean(value) },
            decoder = { buf -> buf.readBoolean() }
        )
        val mtsFix by registerSyncable(
            default = true,
            codec = BOOL,
            encoder = { buf, value -> buf.writeBoolean(value) },
            decoder = { buf -> buf.readBoolean() }
        )

        fun firstAidCompat(): Boolean = firstAidCompat.syncedValue
        fun lsoCompat(): Boolean = lsoCompat.syncedValue
        fun vsCollisionCompat(): Boolean = vsCollisionCompat.syncedValue
        fun vsExplosionCompat(): Boolean = vsExplosionCompat.syncedValue
        fun mtsFix(): Boolean = mtsFix.syncedValue
    }

    object Tweaks : CodecConfig<Tweaks>() {
        val alwaysFilterByHand by register(true, BOOL)
        val suppressHeadHitSounds by register(false, BOOL)
        val suppressFleshHitSounds by register(false, BOOL)
        val suppressKillSounds by register(false, BOOL)
        val hideHitMarkers by register(false, BOOL)

        fun alwaysFilterByHand(): Boolean = alwaysFilterByHand.value
        fun suppressHeadHitSounds(): Boolean = suppressHeadHitSounds.value
        fun suppressFleshHitSounds(): Boolean = suppressFleshHitSounds.value
        fun suppressKillSounds(): Boolean = suppressKillSounds.value
        fun hideHitMarkers(): Boolean = hideHitMarkers.value
    }

    fun generateConfigScreen(parent: Screen?): Screen = YetAnotherConfigLib.createBuilder().apply {
        title(TaCZTweaks.translatable("config.title"))
        save {
            if (ConfigManager.syncedWithServer && ConfigManager.canUpdateServerConfig()) {
                NetworkHandler.sendC2S(ClientMessageSyncConfig())
            } else {
                sync(ESyncDirection.NONE)
            }
            runAsSaving(::saveToFile)
        }

        category(ConfigCategory.createBuilder().apply {
            name(TaCZTweaks.translatable("config.category.general"))
            var canUpdateServerConfig = true
            if (ConfigManager.syncedWithServer) {
                canUpdateServerConfig = ConfigManager.canUpdateServerConfig()
                option(LabelOption.createBuilder().apply {
                    line(TaCZTweaks.translatable("config.label.displayingServerConfigNotice").withStyle(ChatFormatting.BOLD))
                    val key = if (canUpdateServerConfig) {
                        "config.label.configSyncNotice"
                    } else {
                        "config.label.insufficientPermissionNotice"
                    }
                    line(TaCZTweaks.translatable(key))
                }.build())
            }
            group(OptionGroup.createBuilder().apply {
                name(TaCZTweaks.translatable("config.gun"))
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.gun.shootWhileSprinting.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.gun.shootWhileSprinting.description")))
                    binding(Gun.shootWhileSprinting.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.gun.sprintWhileReloading.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.gun.sprintWhileReloading.description")))
                    binding(Gun.sprintWhileReloading.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.gun.reloadWhileShooting.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.gun.reloadWhileShooting.description")))
                    binding(Gun.reloadWhileShooting.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.gun.allowUnload.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.gun.allowUnload.description")))
                    binding(Gun.allowUnload.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
            }.build())
            group(OptionGroup.createBuilder().apply {
                name(TaCZTweaks.translatable("config.crawl"))
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.crawl.enabled.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.crawl.enabled.description")))
                    binding(Crawl.enabled.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
            }.build())
            group(OptionGroup.createBuilder().apply {
                name(TaCZTweaks.translatable("config.compat"))
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.compat.firstAidCompat.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.compat.firstAidCompat.description")))
                    binding(Compat.firstAidCompat.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.compat.lsoCompat.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.compat.lsoCompat.description")))
                    binding(Compat.lsoCompat.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.compat.vsCollisionCompat.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.compat.vsCollisionCompat.description")))
                    binding(Compat.vsCollisionCompat.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.compat.vsExplosionCompat.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.compat.vsExplosionCompat.description")))
                    binding(Compat.vsExplosionCompat.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.compat.mtsFix.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.compat.mtsFix.description")))
                    binding(Compat.mtsFix.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
            }.build())
        }.build())
        category(ConfigCategory.createBuilder().apply {
            name(TaCZTweaks.translatable("config.category.client"))
            group(OptionGroup.createBuilder().apply {
                name(TaCZTweaks.translatable("config.gun"))
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.gun.disableBulletCulling.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.gun.disableBulletCulling.description")))
                    binding(Gun.disableBulletCulling.asBinding())
                    controller(booleanController())
                }.build())
            }.build())
            group(OptionGroup.createBuilder().apply {
                name(TaCZTweaks.translatable("config.crawl"))
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
                name(TaCZTweaks.translatable("config.tweaks"))
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.tweaks.alwaysFilterByHand.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.tweaks.alwaysFilterByHand.description")))
                    binding(Tweaks.alwaysFilterByHand.asBinding())
                    controller(booleanController())
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.tweaks.suppressHeadHitSounds.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.tweaks.suppressHeadHitSounds.description")))
                    binding(Tweaks.suppressHeadHitSounds.asBinding())
                    controller(booleanController())
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.tweaks.suppressFleshHitSounds.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.tweaks.suppressFleshHitSounds.description")))
                    binding(Tweaks.suppressFleshHitSounds.asBinding())
                    controller(booleanController())
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.tweaks.suppressKillSounds.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.tweaks.suppressKillSounds.description")))
                    binding(Tweaks.suppressKillSounds.asBinding())
                    controller(booleanController())
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.tweaks.hideHitMarkers.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.tweaks.hideHitMarkers.description")))
                    binding(Tweaks.hideHitMarkers.asBinding())
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
