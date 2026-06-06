pluginManagement {
    repositories {
        exclusiveContent {
            forRepository {
                maven("https://maven.fabricmc.net")
            }
            filter {
                includeGroupAndSubgroups("net.fabricmc")
                includeGroup("fabric-loom")
            }
        }
        maven("https://maven.kikugie.dev/snapshots") {
            content {
                includeGroupAndSubgroups("dev.kikugie")
            }
        }
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases")
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9.5"
}

stonecutter {
    create(rootProject) {
        fun mc(mcVersion: String, loaders: Iterable<String>) =
            loaders.forEach { loader -> version(project = "$mcVersion-$loader", version = mcVersion).buildscript("$loader.gradle.kts") }

        mc("1.20.1", listOf("fabric", "forge"))
        mc("1.21.1", listOf("fabric", "neoforge"))
        vcsVersion = "1.20.1-forge"
    }
}

rootProject.name = "tacz-tweaks"