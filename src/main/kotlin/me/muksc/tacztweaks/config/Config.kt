package me.muksc.tacztweaks.config

import com.mojang.serialization.Codec
import com.tacz.guns.resource.modifier.AttachmentPropertyManager
import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.config.v3.ConfigEntry
import dev.isxander.yacl3.config.v3.default
import dev.isxander.yacl3.config.v3.register
import dev.isxander.yacl3.config.v3.value
import dev.isxander.yacl3.dsl.*
import dev.isxander.yacl3.platform.YACLPlatform
import io.netty.buffer.ByteBuf
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.config.migration.migrate
import me.muksc.tacztweaks.config.sync.SyncableCodecConfig
import me.muksc.tacztweaks.config.sync.SyncableConfigEntry
import me.muksc.tacztweaks.config.sync.SyncableJsonFileCodecConfig
import me.muksc.tacztweaks.core.camelToSnakeCase
import me.muksc.tacztweaks.core.codec.ByteBufCodecs
import me.muksc.tacztweaks.core.codec.StreamCodec
import me.muksc.tacztweaks.core.extension.isDefault
import me.muksc.tacztweaks.core.logger.withMarker
import me.muksc.tacztweaks.mixin.accessor.ModifierAccessor
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.StringRepresentable
import org.slf4j.MarkerFactory
import java.text.DecimalFormat
import kotlin.reflect.KProperty0
import com.tacz.guns.resource.pojo.data.attachment.Modifier as TaCZModifier

object Config : SyncableJsonFileCodecConfig<Config>(
    YACLPlatform.getConfigDir().resolve("${TaCZTweaks.MOD_ID}.json")
) {
    val logger = TaCZTweaks.logger.withMarker(
        MarkerFactory.getMarker("Config")
    )

    val version by register(1, INT)

    init {
        registerSyncable("general", General)
        registerSyncable("keyActions", KeyActions)
        registerSyncable("gameplay", Gameplay)
        registerSyncable("audioAndVisuals", AudioAndVisuals)
        registerSyncable("balancing", Balancing)
    }

    object General : SyncableCodecConfig<General>() {
        init {
            registerSyncable("compatibility", Compatibility)
            registerSyncable("fixes", Fixes)
            registerSyncable("miscellaneous", Miscellaneous)
        }

        object Compatibility : SyncableCodecConfig<Compatibility>() {
            val forceDisableCrawl by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val firstAidCompat by registerSyncable(true, BOOL, ByteBufCodecs.BOOL)
            val lsoCompat by registerSyncable(true, BOOL, ByteBufCodecs.BOOL)
            val cuffedCompat by registerSyncable(true, BOOL, ByteBufCodecs.BOOL)
            val vsCompat by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val sableCompat by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val mtsCompat by registerSyncable(true, BOOL, ByteBufCodecs.BOOL)

            @JvmStatic fun forceDisableCrawl(): Boolean = forceDisableCrawl.syncedValue
            @JvmStatic fun firstAidCompat(): Boolean = firstAidCompat.syncedValue
            @JvmStatic fun lsoCompat(): Boolean = lsoCompat.syncedValue
            @JvmStatic fun cuffedCompat(): Boolean = cuffedCompat.syncedValue
            @JvmStatic fun vsCompat(): Boolean = vsCompat.syncedValue
            @JvmStatic fun sableCompat(): Boolean = sableCompat.syncedValue
            @JvmStatic fun mtsCompat(): Boolean = mtsCompat.syncedValue
        }

        object Fixes : SyncableCodecConfig<Fixes>() {
            val crawlCooldownFix by registerSyncable(true, BOOL, ByteBufCodecs.BOOL)
            val thirdPersonGunRenderingFix by register(true, BOOL)
            val attachmentCompatibilityCheckFix by register(true, BOOL)
            val disableBulletCulling by register(true, BOOL)

            @JvmStatic fun crawlCooldownFix(): Boolean = crawlCooldownFix.syncedValue
            @JvmStatic fun thirdPersonGunRenderingFix(): Boolean = thirdPersonGunRenderingFix.value
            @JvmStatic fun attachmentCompatibilityCheckFix(): Boolean = attachmentCompatibilityCheckFix.value
            @JvmStatic fun disableBulletCulling(): Boolean = disableBulletCulling.value
        }

        object Miscellaneous : SyncableCodecConfig<Miscellaneous>() {
            val alwaysFilterByItemInHand by register(true, BOOL)
            val dynamicAttachmentSlots by register(false, BOOL)
            val rps by register(false, BOOL)

            @JvmStatic fun alwaysFilterByItemInHand(): Boolean = alwaysFilterByItemInHand.value
            @JvmStatic fun dynamicAttachmentSlots(): Boolean = dynamicAttachmentSlots.value
            @JvmStatic fun rps(): Boolean = rps.value
        }

        object Debug : SyncableCodecConfig<Debug>() {
            val bulletInteractions by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val bulletParticles by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val bulletSounds by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val meleeInteractions by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)

            @JvmStatic fun bulletInteractions(): Boolean = bulletInteractions.syncedValue
            @JvmStatic fun bulletParticles(): Boolean = bulletParticles.syncedValue
            @JvmStatic fun bulletSounds(): Boolean = bulletSounds.syncedValue
            @JvmStatic fun meleeInteractions(): Boolean = meleeInteractions.syncedValue
        }
    }

    object KeyActions : SyncableCodecConfig<KeyActions>() {
        init {
            registerSyncable("unload", Unload)
            registerSyncable("reduceSensitivity", ReduceSensitivity)
            registerSyncable("tiltGun", TiltGun)
        }

        object Unload : SyncableCodecConfig<Unload>() {
            val enabled by registerSyncable(true, BOOL, ByteBufCodecs.BOOL)
            val unloadRoundInChamber by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)

            @JvmStatic fun enabled(): Boolean = enabled.syncedValue
            @JvmStatic fun unloadRoundInChamber(): Boolean = unloadRoundInChamber.syncedValue
        }

        object ReduceSensitivity : SyncableCodecConfig<ReduceSensitivity>() {
            val type by register(EKeyType.HOLD, EKeyType.CODEC)
            val sensitivityMultiplier by register(0.5, DOUBLE)
            val disableWhileAiming by register(false, BOOL)

            @JvmStatic fun type(): EKeyType = type.value
            @JvmStatic fun sensitivityMultiplier(): Double = sensitivityMultiplier.value
            @JvmStatic fun disableWhileAiming(): Boolean = disableWhileAiming.value
        }

        object TiltGun : SyncableCodecConfig<TiltGun>() {
            val type by register(EKeyType.HOLD, EKeyType.CODEC)
            val cancelSprint by register(true, BOOL)
            val reduceSensitivity by register(true, BOOL)

            @JvmStatic fun type(): EKeyType = type.value
            @JvmStatic fun cancelSprint(): Boolean = cancelSprint.value
            @JvmStatic fun reduceSensitivity(): Boolean = reduceSensitivity.value
        }

        enum class EKeyType : StringRepresentable {
            HOLD,
            TOGGLE;

            fun isToggle(): Boolean = this == TOGGLE

            override fun getSerializedName(): String = name

            companion object {
                val CODEC: Codec<EKeyType> = StringRepresentable.fromEnum(::values)
            }
        }
    }

    object Gameplay : SyncableCodecConfig<Gameplay>() {
        init {
            registerSyncable("handling", Handling)
            registerSyncable("behaviour", Behaviour)
            registerSyncable("crawl", Crawl)
        }

        object Handling : SyncableCodecConfig<Handling>() {
            val shootWhileSprinting by registerSyncable(true, BOOL, ByteBufCodecs.BOOL)
            val reloadWhileSprinting by registerSyncable(true, BOOL, ByteBufCodecs.BOOL)
            val reloadInterruptsShooting by registerSyncable(true, BOOL, ByteBufCodecs.BOOL)
            val fireSelectInterruptsShooting by registerSyncable(true, BOOL, ByteBufCodecs.BOOL)
            val manualBolting by registerSyncable(EManualBoltingType.DISABLED, EManualBoltingType.CODEC, EManualBoltingType.STREAM_CODEC)
            val disableUnderwater by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val disableWhileRowing by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val disableRefitOnAdventure by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)

            enum class EManualBoltingType : StringRepresentable {
                DISABLED,
                ENABLED,
                ENABLED_KEY_ONLY;

                override fun getSerializedName(): String = name

                companion object {
                    val CODEC: Codec<EManualBoltingType> = StringRepresentable.fromEnum(::values)
                    val STREAM_CODEC: StreamCodec<ByteBuf, EManualBoltingType> = ByteBufCodecs.idMapper(entries::get, EManualBoltingType::ordinal)
                }
            }

            @JvmStatic fun shootWhileSprinting(): Boolean = shootWhileSprinting.syncedValue
            @JvmStatic fun reloadWhileSprinting(): Boolean = reloadWhileSprinting.syncedValue
            @JvmStatic fun reloadInterruptsShooting(): Boolean = reloadInterruptsShooting.syncedValue
            @JvmStatic fun fireSelectInterruptsShooting(): Boolean = fireSelectInterruptsShooting.syncedValue
            @JvmStatic fun manualBolting(): EManualBoltingType = manualBolting.syncedValue
            @JvmStatic fun disableUnderwater(): Boolean = disableUnderwater.syncedValue
            @JvmStatic fun disableWhileRowing(): Boolean = disableWhileRowing.syncedValue
            @JvmStatic fun disableRefitOnAdventure(): Boolean = disableRefitOnAdventure.syncedValue
        }

        object Behaviour : SyncableCodecConfig<Behaviour>() {
            val realisticInaccuracy by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val tiltRework by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val magazineStyleReloading by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val magazineStyleReloadingExclusions by registerSyncable(
                default = listOf("tacz:m870", "tacz:db_short", "tacz:db_long"),
                codec = Codec.list(STRING),
                streamCodec = ByteBufCodecs.collection(::ArrayList, ByteBufCodecs.STRING_UTF8)
            )
            val bulletProtection by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val endermenEvadeBullets by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val infiniteAmmoDisablesConsumption by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)

            @JvmStatic fun realisticInaccuracy(): Boolean = realisticInaccuracy.syncedValue
            @JvmStatic fun tiltRework(): Boolean = tiltRework.syncedValue
            @JvmStatic fun magazineStyleReloading(): Boolean = magazineStyleReloading.syncedValue
            @JvmStatic fun magazineStyleReloadingExclusions(): List<String> = magazineStyleReloadingExclusions.syncedValue
            @JvmStatic fun bulletProtection(): Boolean = bulletProtection.syncedValue
            @JvmStatic fun endermenEvadeBullets(): Boolean = endermenEvadeBullets.syncedValue
            @JvmStatic fun infiniteAmmoDisablesConsumption(): Boolean = infiniteAmmoDisablesConsumption.syncedValue
        }

        object Crawl : SyncableCodecConfig<Crawl>() {
            val pitchUpperLimit by registerSyncable(25.0F, FLOAT, ByteBufCodecs.FLOAT)
            val pitchLowerLimit by registerSyncable(-10.0F, FLOAT, ByteBufCodecs.FLOAT)
            val dynamicPitchLimit by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)

            @JvmStatic fun pitchUpperLimit(): Float = pitchUpperLimit.syncedValue
            @JvmStatic fun pitchLowerLimit(): Float = pitchLowerLimit.syncedValue
            @JvmStatic fun dynamicPitchLimit(): Boolean = dynamicPitchLimit.syncedValue
        }
    }

    object AudioAndVisuals : SyncableCodecConfig<AudioAndVisuals>() {
        init {
            registerSyncable("system", System)
            registerSyncable("audio", Audio)
            registerSyncable("visuals", Visuals)
        }

        object System : SyncableCodecConfig<System>() {
            val mixToMono by register(true, BOOL)

            @JvmStatic fun mixToMono(): Boolean = mixToMono.value
        }

        object Audio : SyncableCodecConfig<Audio>() {
            val broadcastFirstPersonGunSounds by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val forceFirstPersonShootingSound by registerSyncable(false, BOOL, ByteBufCodecs.BOOL)
            val forceDefaultHitAndKillSounds by register(false, BOOL)
            val suppressHeadshotSounds by register(false, BOOL)
            val suppressHitSounds by register(false, BOOL)
            val suppressKillSounds by register(false, BOOL)

            @JvmStatic fun broadcastFirstPersonGunSounds(): Boolean = broadcastFirstPersonGunSounds.syncedValue
            @JvmStatic fun forceFirstPersonShootingSound(): Boolean = forceFirstPersonShootingSound.syncedValue
            @JvmStatic fun forceDefaultHitAndKillSounds(): Boolean = forceDefaultHitAndKillSounds.value
            @JvmStatic fun suppressHeadshotSounds(): Boolean = suppressHeadshotSounds.value
            @JvmStatic fun suppressHitSounds(): Boolean = suppressHitSounds.value
            @JvmStatic fun suppressKillSounds(): Boolean = suppressKillSounds.value
        }

        object Visuals : SyncableCodecConfig<Visuals>() {
            val stopAimingWhileReloading by register(false, BOOL)
            val stopAimingWhileBolting by register(false, BOOL)
            val tiltOnCrawl by register(ETiltOnCrawl.DEFAULT, ETiltOnCrawl.CODEC)
            val smootherCrawlAnimation by register(true, BOOL)
            val cancelInspection by register(true, BOOL)
            val hideHitMarkers by register(false, BOOL)

            enum class ETiltOnCrawl : StringRepresentable {
                DEFAULT,
                ALWAYS,
                NEVER;

                override fun getSerializedName(): String = name

                companion object {
                    val CODEC: Codec<ETiltOnCrawl> = StringRepresentable.fromEnum(::values)
                }
            }

            @JvmStatic fun stopAimingWhileReloading(): Boolean = stopAimingWhileReloading.value
            @JvmStatic fun stopAimingWhileBolting(): Boolean = stopAimingWhileBolting.value
            @JvmStatic fun tiltOnCrawl(): ETiltOnCrawl = tiltOnCrawl.value
            @JvmStatic fun smootherCrawlAnimation(): Boolean = smootherCrawlAnimation.value
            @JvmStatic fun cancelInspection(): Boolean = cancelInspection.value
            @JvmStatic fun hideHitMarkers(): Boolean = hideHitMarkers.value
        }
    }

    object Balancing : SyncableCodecConfig<Balancing>() {
        private val modifiers = mutableListOf<Modifier>()
        val MODIFIERS: List<Modifier> get() = modifiers

        @JvmField val Damage = modifier("damage")
        @JvmField val PlayerDamage = modifier("playerDamage")
        @JvmField val ExplosionDamage = modifier("explosionDamage")
        @JvmField val PlayerExplosionDamage = modifier("playerExplosionDamage")
        @JvmField val Headshot = modifier("headshot")
        @JvmField val PlayerHeadshot = modifier("playerHeadshot")
        @JvmField val ArmorIgnore = modifier("armorIgnore")
        @JvmField val RPM = modifier("rpm")
        @JvmField val Capacity = modifier("capacity")
        @JvmField val Speed = modifier("speed")
        @JvmField val Gravity = modifier("gravity")
        @JvmField val Friction = modifier("friction")
        @JvmField val AimTime = modifier("aimTime")
        @JvmField val Inaccuracy = modifier("inaccuracy")
        @JvmField val StandInaccuracy = modifier("standInaccuracy")
        @JvmField val AimInaccuracy = modifier("aimInaccuracy")
        @JvmField val MoveInaccuracy = modifier("moveInaccuracy")
        @JvmField val SneakInaccuracy = modifier("sneakInaccuracy")
        @JvmField val CrawlInaccuracy = modifier("crawlInaccuracy")
        @JvmField val VerticalRecoil = modifier("verticalRecoil")
        @JvmField val HorizontalRecoil = modifier("horizontalRecoil")
        @JvmField val AimVerticalRecoil = modifier("aimVerticalRecoil")
        @JvmField val AimHorizontalRecoil = modifier("aimHorizontalRecoil")
        @JvmField val CrawlVerticalRecoil = modifier("crawlVerticalRecoil")
        @JvmField val CrawlHorizontalRecoil = modifier("crawlHorizontalRecoil")

        private fun modifier(key: String): Modifier = Modifier(key).also {
            registerSyncable(key, it)
            modifiers.add(it)
        }

        class Modifier(val key: String) : SyncableCodecConfig<Modifier>() {
            val addend by registerSyncable(0.0, DOUBLE, ByteBufCodecs.DOUBLE)
            val multiplier by registerSyncable(1.0, DOUBLE, ByteBufCodecs.DOUBLE)
            val function by registerSyncable("", STRING, ByteBufCodecs.STRING_UTF8)

            fun eval(value: Double): Double {
                if (isDefault()) return value
                return AttachmentPropertyManager.eval(modifier, value)
            }

            private val _modifier = TaCZModifier()
            private val accessor = _modifier as ModifierAccessor
            val modifier: TaCZModifier get() = _modifier.apply {
                accessor.setAddend(this@Modifier.addend.syncedValue)
                accessor.setMultiplier(this@Modifier.multiplier.syncedValue)
                accessor.setFunction(this@Modifier.function.syncedValue)
            }
        }
    }

    fun initialize() {
        migrate(version.default)
        loadFromFile()
        saveToFile()
    }

    @Suppress("DuplicatedCode")
    fun generateConfigScreen(parent: Screen?): Screen = buildYetAnotherConfigLib {
        category("general") {
            group("compatibility") {
                optionSynced(General.Compatibility::forceDisableCrawl) {
                    builder.controller(booleanController())
                }
                //? if 1.20.1 && forge {
                optionSynced(General.Compatibility::firstAidCompat) {
                    builder.controller(booleanController())
                }
                //?}
                //? if (1.20.1 && forge) || (1.21.1 && neoforge) {
                optionSynced(General.Compatibility::lsoCompat) {
                    builder.controller(booleanController())
                }
                //?}
                //? if 1.20.1 && forge {
                optionSynced(General.Compatibility::cuffedCompat) {
                    builder.controller(booleanController())
                }
                //?}
                //? if 1.20.1 {
                optionSynced(General.Compatibility::vsCompat) {
                    builder.controller(booleanController())
                }
                //?}
                //? if 1.21.1 {
                /*optionSynced(General.Compatibility::sableCompat) {
                    builder.controller(booleanController())
                }
                *///?}
                //? if (1.20.1 && forge) || (1.21.1 && neoforge) {
                optionSynced(General.Compatibility::mtsCompat) {
                    builder.controller(booleanController())
                }
                //?}
            }
            group("fixes") {
                option(General.Fixes::crawlCooldownFix, image = true) {
                    builder.controller(booleanController())
                }
                option(General.Fixes::thirdPersonGunRenderingFix, image = true) {
                    builder.controller(booleanController())
                }
                option(General.Fixes::attachmentCompatibilityCheckFix) {
                    builder.controller(booleanController())
                }
                option(General.Fixes::disableBulletCulling) {
                    builder.controller(booleanController())
                }
            }
            group("miscellaneous") {
                option(General.Miscellaneous::alwaysFilterByItemInHand) {
                    builder.controller(booleanController())
                }
                option(General.Miscellaneous::dynamicAttachmentSlots) {
                    builder.controller(booleanController())
                }
                option(General.Miscellaneous::rps) {
                    builder.controller(booleanController())
                }
            }
            group("debug") {
                option(General.Debug::bulletInteractions) {
                    builder.controller(booleanController())
                }
                option(General.Debug::bulletParticles) {
                    builder.controller(booleanController())
                }
                option(General.Debug::bulletSounds) {
                    builder.controller(booleanController())
                }
                option(General.Debug::meleeInteractions) {
                    builder.controller(booleanController())
                }
            }
        }
        category("keyActions") {
            group("unload") {
                optionSynced(KeyActions.Unload::enabled) {
                    builder.controller(booleanController())
                }
                optionSynced(KeyActions.Unload::unloadRoundInChamber) {
                    builder.controller(booleanController())
                }
            }
            group("reduceSensitivity") {
                option(KeyActions.ReduceSensitivity::type) {
                    builder.controller(enumController())
                }
                option(KeyActions.ReduceSensitivity::sensitivityMultiplier) {
                    builder.controller(slider(range = 0.0..1.0, step = 0.01) {
                        TaCZTweaks.translatable("config.label.multiplier", "%.2f".format(it))
                    })
                }
                option(KeyActions.ReduceSensitivity::disableWhileAiming) {
                    builder.controller(booleanController())
                }
            }
            group("tiltGun") {
                option(KeyActions.TiltGun::type) {
                    builder.controller(enumController())
                }
                option(KeyActions.TiltGun::cancelSprint) {
                    builder.controller(booleanController())
                }
                option(KeyActions.TiltGun::reduceSensitivity) {
                    builder.controller(booleanController())
                }
            }
        }
        category("gameplay") {
            group("handling") {
                optionSynced(Gameplay.Handling::shootWhileSprinting, image = true) {
                    builder.controller(booleanController())
                }
                optionSynced(Gameplay.Handling::reloadWhileSprinting, image = true) {
                    builder.controller(booleanController())
                }
                optionSynced(Gameplay.Handling::reloadInterruptsShooting, image = true) {
                    builder.controller(booleanController())
                }
                optionSynced(Gameplay.Handling::fireSelectInterruptsShooting, image = true) {
                    builder.controller(booleanController())
                }
                optionSynced(Gameplay.Handling::manualBolting) {
                    builder.controller(enumController { when (it) {
                        Gameplay.Handling.EManualBoltingType.DISABLED -> withStyle(ChatFormatting.RED)
                        else -> withStyle(ChatFormatting.GREEN)
                    } })
                }
                optionSynced(Gameplay.Handling::disableUnderwater) {
                    builder.controller(booleanController())
                }
                optionSynced(Gameplay.Handling::disableWhileRowing) {
                    builder.controller(booleanController())
                }
                optionSynced(Gameplay.Handling::disableRefitOnAdventure) {
                    builder.controller(booleanController())
                }
            }
            group("behaviour") {
                optionSynced(Gameplay.Behaviour::realisticInaccuracy) {
                    builder.controller(booleanController())
                }
                optionSynced(Gameplay.Behaviour::tiltRework) {
                    builder.controller(booleanController())
                }
                optionSynced(Gameplay.Behaviour::magazineStyleReloading) {
                    builder.controller(booleanController())
                }
                //? if 1.20.1 {
                optionSynced(Gameplay.Behaviour::bulletProtection) {
                    builder.controller(booleanController())
                }
                //?}
                optionSynced(Gameplay.Behaviour::endermenEvadeBullets) {
                    builder.controller(booleanController())
                }
                optionSynced(Gameplay.Behaviour::infiniteAmmoDisablesConsumption) {
                    builder.controller(booleanController())
                }
            }
            listOptionSynced(Gameplay.Behaviour::magazineStyleReloadingExclusions, group = "behaviour") {
                builder.controller(stringField())
                builder.collapsed(true)
                builder.initial("")
            }
            group("crawl") {
                optionSynced(Gameplay.Crawl::pitchUpperLimit) {
                    builder.controller(slider(range = 0.0F..90.0F, step = 1.0F) {
                        TaCZTweaks.translatable("config.label.degree", "%.1f".format(it))
                    })
                }
                optionSynced(Gameplay.Crawl::pitchLowerLimit) {
                    builder.controller(slider(range = -90.0F..0.0F, step = 1.0F) {
                        TaCZTweaks.translatable("config.label.degree", "%.1f".format(it))
                    })
                }
                optionSynced(Gameplay.Crawl::dynamicPitchLimit) {
                    builder.controller(booleanController())
                }
            }
        }
        category("audioAndVisuals") {
            group("system") {
                option(AudioAndVisuals.System::mixToMono) {
                    builder.controller(booleanController())
                }
            }
            group("audio") {
                optionSynced(AudioAndVisuals.Audio::broadcastFirstPersonGunSounds) {
                    builder.controller(booleanController())
                }
                optionSynced(AudioAndVisuals.Audio::forceFirstPersonShootingSound) {
                    builder.controller(booleanController())
                }
                option(AudioAndVisuals.Audio::forceDefaultHitAndKillSounds) {
                    builder.controller(booleanController())
                }
                option(AudioAndVisuals.Audio::suppressHeadshotSounds) {
                    builder.controller(booleanController())
                }
                option(AudioAndVisuals.Audio::suppressHitSounds) {
                    builder.controller(booleanController())
                }
                option(AudioAndVisuals.Audio::suppressKillSounds) {
                    builder.controller(booleanController())
                }
            }
            group("visuals") {
                option(AudioAndVisuals.Visuals::stopAimingWhileReloading, image = true) {
                    builder.controller(booleanController())
                }
                option(AudioAndVisuals.Visuals::stopAimingWhileBolting, image = true) {
                    builder.controller(booleanController())
                }
                option(AudioAndVisuals.Visuals::tiltOnCrawl) {
                    builder.controller(enumController())
                }
                option(AudioAndVisuals.Visuals::smootherCrawlAnimation) {
                    builder.controller(booleanController())
                }
                option(AudioAndVisuals.Visuals::cancelInspection) {
                    builder.controller(booleanController())
                }
                option(AudioAndVisuals.Visuals::hideHitMarkers) {
                    builder.controller(booleanController())
                }
            }
        }
        category("balancing") {
            for (modifier in Balancing.MODIFIERS) {
                group(modifier.key) {
                    builder.collapsed(true)
                    optionSynced(modifier::addend, namespace = "${this@category.namespace}.modifier") {
                        builder.controller(numberField { value: Double ->
                            Component.literal(DecimalFormat("+#.#;-#.#").format(value))
                        })
                    }
                    optionSynced(modifier::multiplier, namespace = "${this@category.namespace}.modifier") {
                        builder.controller(numberField { value: Double ->
                            Component.literal("%.1f".format(value))
                        })
                    }
                    optionSynced(modifier::function, namespace = "${this@category.namespace}.modifier") {
                        builder.controller(stringField())
                    }
                }
            }
        }
    }.generateScreen(parent)

    @DslMarker annotation class YetAnotherConfigLibDsl
    @YetAnotherConfigLibDsl class YetAnotherConfigLibBuilder(val builder: YetAnotherConfigLib.Builder)
    @YetAnotherConfigLibDsl class ConfigCategoryBuilder(val namespace: String, val builder: ConfigCategory.Builder)
    @YetAnotherConfigLibDsl class OptionGroupBuilder(val namespace: String, val builder: OptionGroup.Builder)
    @YetAnotherConfigLibDsl class OptionBuilder<T>(val namespace: String, val builder: Option.Builder<T>)
    @YetAnotherConfigLibDsl class ListOptionBuilder<T>(val namespace: String, val builder: ListOption.Builder<T>)

    private fun buildYetAnotherConfigLib(block: YetAnotherConfigLibBuilder.() -> Unit): YetAnotherConfigLib =
        YetAnotherConfigLib.createBuilder().apply {
            title(TaCZTweaks.translatable("config.title"))
            save(ConfigManager::saveAndSync)
            block(YetAnotherConfigLibBuilder(this))
        }.build()

    private fun YetAnotherConfigLibBuilder.category(name: String, block: ConfigCategoryBuilder.() -> Unit): YetAnotherConfigLibBuilder = apply {
        builder.category(ConfigCategory.createBuilder().apply {
            name(TaCZTweaks.translatable("config.category.$name.name"))
            tooltip(TaCZTweaks.translatable("config.category.$name.tooltip"))
            block(ConfigCategoryBuilder(name, this))
        }.build())
    }

    private fun ConfigCategoryBuilder.group(name: String, block: OptionGroupBuilder.() -> Unit): ConfigCategoryBuilder = apply {
        builder.group(OptionGroup.createBuilder().apply {
            val namespace = "$namespace.$name"
            name(TaCZTweaks.translatable("config.group.$namespace.name"))
            description(TaCZTweaks.translatable("config.group.$namespace.description"))
            block(OptionGroupBuilder(namespace, this))
        }.build())
    }

    private fun <T> OptionGroupBuilder.option(name: String, image: Boolean = false, block: OptionBuilder<T>.() -> Unit): OptionGroupBuilder = apply {
        builder.option(Option.createBuilder<T>().apply {
            val namespace = "$namespace.$name"
            val path = namespace.replace('.', '/').camelToSnakeCase()
            name(TaCZTweaks.translatable("config.option.$namespace.name"))
            description(OptionDescription.createBuilder().apply {
                text(TaCZTweaks.translatable("config.option.$namespace.description"))
                if (image) webpImage(TaCZTweaks.id("textures/gui/config/${path}.webp"))
            }.build())
            block(OptionBuilder(namespace, this))
        }.build())
    }

    private fun <T> OptionGroupBuilder.option(property: KProperty0<ConfigEntry<T>>, image: Boolean = false, block: OptionBuilder<T>.() -> Unit): OptionGroupBuilder =
        option(property.name, image) {
            builder.binding(property.get().asBinding())
            block(this)
        }

    private fun <T> OptionGroupBuilder.optionSynced(name: String, namespace: String? = null, image: Boolean = false, block: OptionBuilder<T>.() -> Unit): OptionGroupBuilder = apply {
        builder.option(Option.createBuilder<T>().apply {
            val namespace = "${namespace ?: this@optionSynced.namespace}.$name"
            val path = namespace.replace('.', '/').camelToSnakeCase()
            name(TaCZTweaks.translatable("config.option.$namespace.name").apply { if (ConfigManager.syncedWithServer) withStyle(ChatFormatting.YELLOW) })
            description(OptionDescription.createBuilder().apply {
                if (ConfigManager.syncedWithServer) {
                    text(TaCZTweaks.translatable("config.label.synced")
                        .append(Component.literal("\n"))
                        .withStyle(ChatFormatting.YELLOW))
                }
                text(TaCZTweaks.translatable("config.option.$namespace.description"))
                if (image) webpImage(TaCZTweaks.id("textures/gui/config/${path}.webp"))
            }.build())
            available(!ConfigManager.syncedWithServer || ConfigManager.canUpdateServerConfig())
            block(OptionBuilder(namespace, this))
        }.build())
    }

    private fun <T> OptionGroupBuilder.optionSynced(property: KProperty0<SyncableConfigEntry<T>>, namespace: String? = null, image: Boolean = false, block: OptionBuilder<T>.() -> Unit): OptionGroupBuilder = apply {
        optionSynced(property.name, namespace, image) {
            builder.binding(property.get().asSyncedBinding())
            block(this)
        }
    }

    private fun <T> ConfigCategoryBuilder.listOption(name: String, block: ListOptionBuilder<T>.() -> Unit): ConfigCategoryBuilder = apply {
        builder.group(ListOption.createBuilder<T>().apply {
            val namespace = "$namespace.$name"
            name(TaCZTweaks.translatable("config.option.$namespace.name"))
            description(TaCZTweaks.translatable("config.option.$namespace.description"))
            block(ListOptionBuilder(namespace, this))
        }.build())
    }

    private fun <T> ConfigCategoryBuilder.listOptionSynced(property: KProperty0<SyncableConfigEntry<List<T>>>, group: String? = null, block: ListOptionBuilder<T>.() -> Unit): ConfigCategoryBuilder =
        listOption( buildString {
            if (group != null) append("$group.")
            append(property.name)
        }) {
            builder.binding(property.get().asSyncedBinding())
            block(this)
        }

    private fun OptionGroup.Builder.description(vararg description: Component): OptionGroup.Builder =
        description(OptionDescription.of(*description))

    private fun <T> ListOption.Builder<T>.description(vararg description: Component): ListOption.Builder<T> =
        description(OptionDescription.of(*description))

    private fun booleanController(): ControllerBuilderFactory<Boolean> = { option ->
        BooleanControllerBuilder.create(option).formatValue { when (it) {
            true -> TaCZTweaks.translatable("config.label.enabled")
            false -> TaCZTweaks.translatable("config.label.disabled")
        } }.coloured(true)
    }

    private inline fun <reified T> OptionBuilder<T>.enumController(
        crossinline block: MutableComponent.(T) -> Unit = { }
    ): ControllerBuilderFactory<T> where T : Enum<T>, T : StringRepresentable = enumSwitch {
        TaCZTweaks.translatable("config.label.enum.${T::class.simpleName}.${it.serializedName}").apply {
            block(it)
        }
    }
}