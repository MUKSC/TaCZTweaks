package me.muksc.tacztweaks

import com.tacz.guns.resource.modifier.AttachmentPropertyManager
import com.tacz.guns.resource.pojo.data.attachment.Modifier
import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.config.v3.register
import dev.isxander.yacl3.config.v3.value
import dev.isxander.yacl3.dsl.ControllerBuilderFactory
import dev.isxander.yacl3.dsl.slider
import dev.isxander.yacl3.dsl.stringField
import dev.isxander.yacl3.platform.YACLPlatform
import me.muksc.tacztweaks.config.ConfigManager
import me.muksc.tacztweaks.config.ESyncDirection
import me.muksc.tacztweaks.config.SyncableCodecConfig
import me.muksc.tacztweaks.config.SyncableJsonFileCodecConfig
import me.muksc.tacztweaks.network.NetworkHandler
import me.muksc.tacztweaks.network.message.ClientMessageSyncConfig
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import java.text.DecimalFormat

@Suppress("UnstableApiUsage")
object Config : SyncableJsonFileCodecConfig<Config>(
    YACLPlatform.getConfigDir().resolve("${TaCZTweaks.MOD_ID}.json")
) {
    init {
        registerSyncable("gun", Gun)
        registerSyncable("modifiers", Modifiers)
        registerSyncable("crawl", Crawl)
        registerSyncable("compat", Compat)
        registerSyncable("tweaks", Tweaks)
    }

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

    abstract class ModifierConfig : SyncableCodecConfig<ModifierConfig>() {
        val addend by registerSyncable(
            default = 0.0F,
            codec = FLOAT,
            encoder = { buf, value -> buf.writeFloat(value) },
            decoder = { buf -> buf.readFloat() }
        )
        val multiplier by registerSyncable(
            default = 1.0F,
            codec = FLOAT,
            encoder = { buf, value -> buf.writeFloat(value) },
            decoder = { buf -> buf.readFloat() }
        )
        val function by registerSyncable<String>(
            default = "",
            codec = STRING,
            encoder = { buf, value -> buf.writeUtf(value) },
            decoder = { buf -> buf.readUtf() }
        )

        fun toTaCZ(): Modifier {
            var modifier = modifier
            if (modifier == null) modifier = create()
            if (modifier.addend.toFloat() != addend.syncedValue) modifier = create()
            if (modifier.multiplier.toFloat() != multiplier.syncedValue) modifier = create()
            if (modifier.function != function.syncedValue.takeIf { it.isNotEmpty() }) modifier = create()
            return modifier.also(this::modifier::set)
        }

        private var modifier: Modifier? = null

        private fun create(): Modifier = Modifier().apply {
            val instance = this
            val config = this@ModifierConfig
            Modifier::class.java.run {
                setPrivateField(instance, "addend", config.addend.syncedValue)
                setPrivateField(instance, "multiplier", config.multiplier.syncedValue)
                setPrivateField(instance, "function", config.function.syncedValue.takeIf { it.isNotEmpty() })
            }
        }
    }

    object Modifiers : SyncableCodecConfig<Modifiers>() {
        init {
            registerSyncable("damage", Damage)
            registerSyncable("playerDamage", PlayerDamage)
            registerSyncable("headshot", Headshot)
            registerSyncable("armorIgnore", ArmorIgnore)
            registerSyncable("speed", Speed)
            registerSyncable("gravity", Gravity)
            registerSyncable("friction", Friction)
            registerSyncable("inaccuracy", Inaccuracy)
            registerSyncable("aimInaccuracy", AimInaccuracy)
            registerSyncable("rpm", RPM)
            registerSyncable("verticalRecoil", VerticalRecoil)
            registerSyncable("horizontalRecoil", HorizontalRecoil)
        }

        object Damage : ModifierConfig()
        object PlayerDamage : ModifierConfig()
        object Headshot : ModifierConfig()
        object ArmorIgnore : ModifierConfig()
        object Speed : ModifierConfig()
        object Gravity : ModifierConfig()
        object Friction : ModifierConfig()
        object Inaccuracy : ModifierConfig()
        object AimInaccuracy : ModifierConfig()
        object RPM : ModifierConfig()
        object VerticalRecoil : ModifierConfig()
        object HorizontalRecoil : ModifierConfig()
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

    object Tweaks : SyncableCodecConfig<Tweaks>() {
        val endermenEvadeBullets by registerSyncable(
            default = false,
            codec = BOOL,
            encoder = { buf, value -> buf.writeBoolean(value) },
            decoder = { buf -> buf.readBoolean() }
        )
        val alwaysFilterByHand by register(true, BOOL)
        val suppressHeadHitSounds by register(false, BOOL)
        val suppressFleshHitSounds by register(false, BOOL)
        val suppressKillSounds by register(false, BOOL)
        val hideHitMarkers by register(false, BOOL)

        fun endermenEvadeBullets(): Boolean = endermenEvadeBullets.syncedValue
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
                run {
                    val server = Minecraft.getInstance().singleplayerServer ?: return@run
                    val player = server.playerList.getPlayer(Minecraft.getInstance().player?.uuid ?: return@run) ?: return@run
                    AttachmentPropertyManager.postChangeEvent(player, player.mainHandItem)
                }
            }
            runAsSaving(::saveToFile)
            Minecraft.getInstance().player?.also { player ->
                AttachmentPropertyManager.postChangeEvent(player, player.mainHandItem)
            }
        }

        val canUpdateServerConfig = when (ConfigManager.syncedWithServer) {
            true -> ConfigManager.canUpdateServerConfig()
            false -> true
        }

        fun <T> Option.Builder<T>.nameSynced(name: MutableComponent) {
            if (ConfigManager.syncedWithServer) name.withStyle(ChatFormatting.YELLOW)
            this.name(name)
        }

        fun <T> Option.Builder<T>.descriptionSynced(description: OptionDescription) {
            this.description(OptionDescription.createBuilder().apply {
                if (ConfigManager.syncedWithServer) {
                    text(TaCZTweaks.translatable("config.label.synced")
                        .append(Component.literal("\n"))
                        .withStyle(ChatFormatting.YELLOW))
                }
                text(description.text())
            }.build())
        }

        category(ConfigCategory.createBuilder().apply {
            name(TaCZTweaks.translatable("config.category.general"))
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
                    nameSynced(TaCZTweaks.translatable("config.compat.firstAidCompat.name"))
                    descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.compat.firstAidCompat.description")))
                    binding(Compat.firstAidCompat.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    nameSynced(TaCZTweaks.translatable("config.compat.lsoCompat.name"))
                    descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.compat.lsoCompat.description")))
                    binding(Compat.lsoCompat.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    nameSynced(TaCZTweaks.translatable("config.compat.vsCollisionCompat.name"))
                    descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.compat.vsCollisionCompat.description")))
                    binding(Compat.vsCollisionCompat.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    nameSynced(TaCZTweaks.translatable("config.compat.vsExplosionCompat.name"))
                    descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.compat.vsExplosionCompat.description")))
                    binding(Compat.vsExplosionCompat.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    nameSynced(TaCZTweaks.translatable("config.compat.mtsFix.name"))
                    descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.compat.mtsFix.description")))
                    binding(Compat.mtsFix.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
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
        category(ConfigCategory.createBuilder().apply {
            name(TaCZTweaks.translatable("config.category.gameplay"))
            group(OptionGroup.createBuilder().apply {
                name(TaCZTweaks.translatable("config.gun"))
                option(Option.createBuilder<Boolean>().apply {
                    nameSynced(TaCZTweaks.translatable("config.gun.shootWhileSprinting.name"))
                    descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.gun.shootWhileSprinting.description")))
                    binding(Gun.shootWhileSprinting.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    nameSynced(TaCZTweaks.translatable("config.gun.sprintWhileReloading.name"))
                    descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.gun.sprintWhileReloading.description")))
                    binding(Gun.sprintWhileReloading.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    nameSynced(TaCZTweaks.translatable("config.gun.reloadWhileShooting.name"))
                    descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.gun.reloadWhileShooting.description")))
                    binding(Gun.reloadWhileShooting.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    nameSynced(TaCZTweaks.translatable("config.gun.allowUnload.name"))
                    descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.gun.allowUnload.description")))
                    binding(Gun.allowUnload.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
            }.build())
            group(OptionGroup.createBuilder().apply {
                name(TaCZTweaks.translatable("config.crawl"))
                option(Option.createBuilder<Boolean>().apply {
                    nameSynced(TaCZTweaks.translatable("config.crawl.enabled.name"))
                    descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.crawl.enabled.description")))
                    binding(Crawl.enabled.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
                option(Option.createBuilder<Float>().apply {
                    name(TaCZTweaks.translatable("config.crawl.pitchUpperLimit.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.crawl.pitchUpperLimit.description")))
                    binding(Crawl.pitchUpperLimit.asBinding())
                    controller(slider(range = 0.0F..90.0F, step = 1.0F) {
                        TaCZTweaks.translatable("config.label.degree", "%.1f".format(it))
                    })
                }.build())
                option(Option.createBuilder<Float>().apply {
                    name(TaCZTweaks.translatable("config.crawl.pitchLowerLimit.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.crawl.pitchLowerLimit.description")))
                    binding(Crawl.pitchLowerLimit.asBinding())
                    controller(slider(range = -90.0F..0.0F, step = 1.0F) {
                        TaCZTweaks.translatable("config.label.degree", "%.1f".format(it))
                    })
                }.build())
                option(Option.createBuilder<Boolean>().apply {
                    name(TaCZTweaks.translatable("config.crawl.dynamicPitchLimit.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.crawl.dynamicPitchLimit.description")))
                    binding(Crawl.dynamicPitchLimit.asBinding())
                    controller(booleanController())
                }.build())
            }.build())
            group(OptionGroup.createBuilder().apply {
                name(TaCZTweaks.translatable("config.tweaks"))
                option(Option.createBuilder<Boolean>().apply {
                    nameSynced(TaCZTweaks.translatable("config.tweaks.endermenEvadeBullets.name"))
                    descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.tweaks.endermenEvadeBullets.description")))
                    binding(Tweaks.endermenEvadeBullets.asSyncedBinding())
                    controller(booleanController())
                    available(canUpdateServerConfig)
                }.build())
            }.build())
        }.build())
        category(ConfigCategory.createBuilder().apply {
            name(TaCZTweaks.translatable("config.category.balancing"))
            for ((key, modifier) in listOf(
                "damage" to Modifiers.Damage,
                "playerDamage" to Modifiers.PlayerDamage,
                "headshot" to Modifiers.Headshot,
                "armorIgnore" to Modifiers.ArmorIgnore,
                "speed" to Modifiers.Speed,
                "gravity" to Modifiers.Gravity,
                "friction" to Modifiers.Friction,
                "inaccuracy" to Modifiers.Inaccuracy,
                "aimInaccuracy" to Modifiers.AimInaccuracy,
                "rpm" to Modifiers.RPM,
                "verticalRecoil" to Modifiers.VerticalRecoil,
                "horizontalRecoil" to Modifiers.HorizontalRecoil
            )) {
                group(OptionGroup.createBuilder().apply {
                    name(TaCZTweaks.translatable("config.modifiers.$key.name"))
                    description(OptionDescription.of(TaCZTweaks.translatable("config.modifiers.$key.description")))
                    collapsed(true)
                    option(Option.createBuilder<Float>().apply {
                        nameSynced(TaCZTweaks.translatable("config.modifier.addend.name"))
                        descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.modifier.addend.description")))
                        binding(modifier.addend.asSyncedBinding())
                        controller(slider(range = -100.0F..100.0F, step = 0.1F) {
                            Component.literal(DecimalFormat("+#.#;-#.#").format(it))
                        })
                        available(canUpdateServerConfig)
                    }.build())
                    option(Option.createBuilder<Float>().apply {
                        nameSynced(TaCZTweaks.translatable("config.modifier.multiplier.name"))
                        descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.modifier.multiplier.description")))
                        binding(modifier.multiplier.asSyncedBinding())
                        controller(slider(range = -100.0F..100.0F, step = 0.1F) {
                            Component.literal("%.1f".format(it))
                        })
                        available(canUpdateServerConfig)
                    }.build())
                    option(Option.createBuilder<String>().apply {
                        nameSynced(TaCZTweaks.translatable("config.modifier.function.name"))
                        descriptionSynced(OptionDescription.of(TaCZTweaks.translatable("config.modifier.function.description")))
                        binding(modifier.function.asSyncedBinding())
                        controller(stringField())
                        available(canUpdateServerConfig)
                    }.build())
                }.build())
            }
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
