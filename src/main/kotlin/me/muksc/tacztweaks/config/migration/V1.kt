package me.muksc.tacztweaks.config.migration

import com.google.gson.JsonObject
import me.muksc.tacztweaks.config.Config.Gameplay.Handling.EManualBoltingType
import me.muksc.tacztweaks.core.extension.*

fun migrateToV1(data: JsonObject): JsonObject {
    logger.info("Migrating to V1")
    // Early V3 alpha
    if (data.getJsonObjectOrNull("general") != null) return data.apply {
        addProperty("version", 1)
    }

    return JsonObject().apply {
        addProperty("version", 1)
        add("general", JsonObject().apply {
            add("compatibility", JsonObject().apply {
                data.getJsonObjectOrNull("crawl")?.getBooleanOrNull("enabled")
                    ?.let { addProperty("forceDisableCrawl", !it) }
                data.getJsonObjectOrNull("compat")?.getBooleanOrNull("firstAidCompat")
                    ?.let { addProperty("firstAidCompat", it) }
                data.getJsonObjectOrNull("compat")?.getBooleanOrNull("lsoCompat")
                    ?.let { addProperty("lsoCompat", it) }
                data.getJsonObjectOrNull("compat")?.let { compat ->
                    val vsCollisionCompat = compat.getBooleanOrNull("vsCollisionCompat")
                    val vsExplosionCompat = compat.getBooleanOrNull("vsExplosionCompat")
                    when {
                        vsCollisionCompat == true || vsExplosionCompat == true -> true
                        vsCollisionCompat == false || vsExplosionCompat == false -> false
                        else -> null
                    }
                }?.let { addProperty("vsCompat", it) }
                data.getJsonObjectOrNull("compat")?.getBooleanOrNull("mtsFix")
                    ?.let { addProperty("mtsCompat", it) }
            })
            add("fixes", JsonObject().apply {
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("thirdPersonGunRenderingFix")
                    ?.let { addProperty("thirdPersonGunRenderingFix", it) }
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("disableBulletCulling")
                    ?.let { addProperty("disableBulletCulling", it) }
            })
            add("miscellaneous", JsonObject().apply {
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("alwaysFilterByHand")
                    ?.let { addProperty("alwaysFilterByItemInHand", it) }
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("rps")
                    ?.let { addProperty("rps", it) }
            })
            add("debug", JsonObject().apply {
                data.getJsonObjectOrNull("debug")?.getBooleanOrNull("bulletInteractions")
                    ?.let { addProperty("bulletInteractions", it) }
                data.getJsonObjectOrNull("debug")?.getBooleanOrNull("bulletParticles")
                    ?.let { addProperty("bulletParticles", it) }
                data.getJsonObjectOrNull("debug")?.getBooleanOrNull("bulletSounds")
                    ?.let { addProperty("bulletSounds", it) }
                data.getJsonObjectOrNull("debug")?.getBooleanOrNull("meleeInteractions")
                    ?.let { addProperty("meleeInteractions", it) }
            })
        })
        add("keyActions", JsonObject().apply {
            add("unload", JsonObject().apply {
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("allowUnload")
                    ?.let { addProperty("enabled", it) }
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("unloadBulletInBarrel")
                    ?.let { addProperty("unloadRoundInChamber", it) }
            })
            add("reduceSensitivity", JsonObject().apply {
                data.getJsonObjectOrNull("gun")?.getDoubleOrNull("reduceSensitivityKeyMultiplier")
                    ?.let { addProperty("sensitivityMultiplier", it) }
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("disableReduceSensitivityKeyWhileAiming")
                    ?.let { addProperty("disableWhileAiming", it) }
            })
            add("tiltGun", JsonObject().apply {
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("tiltGunKeyCancelsSprint")
                    ?.let { addProperty("cancelSprint", it) }
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("tiltGunKeyTriggersReduceSensitivity")
                    ?.let { addProperty("reduceSensitivity", it) }
            })
        })
        add("gameplay", JsonObject().apply {
            add("handling", JsonObject().apply {
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("shootWhileSprinting")
                    ?.let { addProperty("shootWhileSprinting", it) }
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("sprintWhileReloading")
                    ?.let { addProperty("reloadWhileSprinting", it) }
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("reloadWhileShooting")
                    ?.let { addProperty("reloadInterruptsShooting", it) }
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("fireSelectWhileShooting")
                    ?.let { addProperty("fireSelectInterruptsShooting", it) }
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("manualBolting")
                    ?.let {
                        addProperty("manualBolting", (if (it) {
                            EManualBoltingType.ENABLED
                        } else {
                            EManualBoltingType.DISABLED
                        }).serializedName)
                    }
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("disableUnderwater")
                    ?.let { addProperty("disableUnderwater", it) }
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("disableRefitOnAdventure")
                    ?.let { addProperty("disableRefitOnAdventure", it) }
            })
            add("behaviour", JsonObject().apply {
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("betterInaccuracy")
                    ?.let { addProperty("realisticInaccuracy", it) }
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("betterGunTilt")
                    ?.let { addProperty("tiltRework", it) }
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("reloadDiscardsMagazine")
                    ?.let { addProperty("magazineStyleReloading", it) }
                data.getJsonObjectOrNull("gun")?.getJsonArrayOrNull("reloadDiscardsMagazineExclusions")
                    ?.let { add("magazineStyleReloadingExclusions", it) }
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("bulletProtection")
                    ?.let { addProperty("bulletProtection", it) }
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("endermenEvadeBullets")
                    ?.let { addProperty("endermenEvadeBullets", it) }
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("infiniteAmmoDisablesConsumption")
                    ?.let { addProperty("infiniteAmmoDisablesConsumption", it) }
            })
            add("crawl", JsonObject().apply {
                data.getJsonObjectOrNull("crawl")?.getFloatOrNull("pitchUpperLimit")
                    ?.let { addProperty("pitchUpperLimit", it) }
                data.getJsonObjectOrNull("crawl")?.getFloatOrNull("pitchLowerLimit")
                    ?.let { addProperty("pitchLowerLimit", it) }
                data.getJsonObjectOrNull("crawl")?.getBooleanOrNull("dynamicPitchLimit")
                    ?.let { addProperty("dynamicPitchLimit", it) }
            })
        })
        add("audioAndVisuals", JsonObject().apply {
            add("system", JsonObject().apply {
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("betterMonoConversion")
                    ?.let { addProperty("mixToMono", it) }
            })
            add("audio", JsonObject().apply {
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("audibleFirstPersonGunSounds")
                    ?.let { addProperty("broadcastFirstPersonGunSounds", it) }
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("forceFirstPersonShootingSound")
                    ?.let { addProperty("forceFirstPersonShootingSound", it) }
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("forceDefaultHitAndKillSounds")
                    ?.let { addProperty("forceDefaultHitAndKillSounds", it) }
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("suppressHeadHitSounds")
                    ?.let { addProperty("suppressHeadshotSounds", it) }
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("suppressFleshHitSounds")
                    ?.let { addProperty("suppressHitSounds", it) }
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("suppressKillSounds")
                    ?.let { addProperty("suppressKillSounds", it) }
            })
            add("visuals", JsonObject().apply {
                data.getJsonObjectOrNull("crawl")?.getBooleanOrNull("tiltGun")
                    ?.let { addProperty("tiltOnCrawl", it) }
                data.getJsonObjectOrNull("crawl")?.getBooleanOrNull("visualTweak")
                    ?.let { addProperty("smootherCrawlAnimation", it) }
                data.getJsonObjectOrNull("gun")?.getBooleanOrNull("cancelInspection")
                    ?.let { addProperty("cancelInspection", it) }
                data.getJsonObjectOrNull("tweaks")?.getBooleanOrNull("hideHitMarkers")
                    ?.let { addProperty("hideHitMarkers", it) }
            })
        })
        data.getJsonObjectOrNull("modifiers")?.let { add("balancing", it) }
    }
}