import org.slf4j.event.Level

plugins {
    id("multiloader-common")
    id("net.neoforged.moddev")
}

neoForge {
    version = libs("neoforge")
    accessTransformers.from(rootProject.file("src/main/resources-neoforge/META-INF/accesstransformer.cfg"))
    validateAccessTransformers = true

    parchment {
        minecraftVersion = prop("minecraft.version")
        mappingsVersion = libs("parchment")
    }

    runs {
        create("client") {
            client()
            programArgument("--username=Dev")
            gameDirectory = project.file("run/client")
        }

        create("client2") {
            client()
            programArgument("--username=Dev2")
            gameDirectory = project.file("run/client2")
        }

        create("server") {
            server()
            programArgument("--nogui")
            gameDirectory = project.file("run/server")
        }

        configureEach {
            jvmArgument("-XX:+AllowEnhancedClassRedefinition")
            logLevel = Level.DEBUG
        }
    }

    mods {
        create(mod("id")) {
            sourceSet(sourceSets["main"])
        }
    }
}

repositories {
    exclusiveContent {
        forRepository {
            maven("https://thedarkcolour.github.io/KotlinForForge")
        }
        filter {
            includeGroup("thedarkcolour")
        }
    }
    mavenCentral()
}

val localRuntime: Configuration by configurations.creating {
    configurations {
        runtimeClasspath {
            extendsFrom(this@creating)
        }
    }
}

dependencies {
    implementation("io.github.llamalad7:mixinextras-neoforge:${libs("mixinextras")}")
    jarJar("io.github.llamalad7:mixinextras-neoforge:${libs("mixinextras")}")
    compileOnly(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:${libs("mixinsquared")}")) { }
    implementation(jarJar("com.github.bawnorton.mixinsquared:mixinsquared-neoforge:${libs("mixinsquared")}")) { }
    implementation("thedarkcolour:kotlinforforge-neoforge:${libs("kff")}")
    implementation("dev.isxander:yet-another-config-lib:${libs("yacl")}")
    run {
        implementation("maven.modrinth:tacz-1.21.1:${libs("tacz")}")
        implementation("org.apache.commons:commons-math3:3.6.1")
    }

    // Compatibility
    compileOnly("maven.modrinth:legendary-survival-overhaul:${libs("lso")}")
    compileOnly("dev.ryanhcode.sable:sable-common-1.21.1:${libs("sable")}")
    compileOnly("maven.modrinth:lr-tactical-1.21.1:${libs("lr-tactical")}")
    compileOnly("maven.modrinth:sound-physics-remastered:${libs("sound-physics-remastered")}")

    // Runtime
    localRuntime("maven.modrinth:better-modlist:${libs("better-modlist")}")
    localRuntime("maven.modrinth:cubes-without-borders:${libs("cubes-without-borders")}")
    run {
        localRuntime("dev.ryanhcode.sable:sable-neoforge-1.21.1:${libs("sable")}") {
            isTransitive = false
        }
    }
    localRuntime("maven.modrinth:sound-physics-remastered:${libs("sound-physics-remastered")}")
}

resourceProperties {
    properties.putAll(mapOf(
        "loader_version" to libs("neoforge"),
        "kotlin_loader_version" to libs("kff").substringBefore('.'),
        "kff_version" to libs("kff"),
        "tacz_version_min" to prop("version.target.min"),
        "tacz_version_max" to prop("version.target.max")
    ))
}

stonecutter replacements {
    run {
        val from = arrayOf(
            "net.minecraftforge.api.distmarker.OnlyIn",
            "net.fabricmc.api.Environment"
        )
        val to = "net.neoforged.api.distmarker.OnlyIn"
        from.forEach { string(true, "environment") { replace(it, to) } }
    }
    run {
        val from = arrayOf(
            "net.minecraftforge.api.distmarker.Dist.CLIENT",
            "net.fabricmc.api.EnvType.CLIENT"
        )
        val to = "net.neoforged.api.distmarker.Dist.CLIENT"
        from.forEach { string(true, "environment_client") { replace(it, to) } }
    }
    run {
        val from = arrayOf(
            "net/minecraftforge/common/ForgeConfigSpec",
        )
        val to = "net/neoforged/neoforge/common/ModConfigSpec"
        from.forEach { string(true, "config_spec") { replace(it, to) } }
    }
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
}

publishMods {
    file = tasks.jar.get().archiveFile
    additionalFiles.from(tasks.sourcesJar.get().archiveFile)
}