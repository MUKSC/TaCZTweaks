import org.slf4j.event.Level

plugins {
    id("multiloader-common")
    id("net.neoforged.moddev.legacyforge")
}

mixin {
    config("${mod("id")}.mixins.json")
    config("${mod("id")}-forge.mixins.json")
    add(sourceSets["main"], "${mod("id")}.refmap.json")
}

tasks.jar {
    manifest.attributes(mapOf(
        "MixinConfigs" to arrayOf(
            "${mod("id")}.mixins.json",
            "${mod("id")}-forge.mixins.json"
        ).joinToString(",")
    ))
}

legacyForge {
    version = "${prop("minecraft.version")}-${libs("forge")}"
    accessTransformers.from(rootProject.file("src/main/resources-forge/META-INF/accesstransformer.cfg"))
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
    exclusiveContent {
        forRepository {
            maven("https://maven.su5ed.dev/releases")
        }
        filter {
            includeGroupAndSubgroups("org.sinytra")
        }
    }
}

configurations.create("localRuntime") {
    configurations {
        runtimeClasspath {
            extendsFrom(this@create)
        }
    }

    obfuscation {
        createRemappingConfiguration(this@create)
    }
}

val modLocalRuntime: Configuration by configurations.getting

dependencies {
    run {
        compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:${libs("mixinextras")}")) { }
        implementation("io.github.llamalad7:mixinextras-forge:${libs("mixinextras")}")
        jarJar("io.github.llamalad7:mixinextras-forge:${libs("mixinextras")}:slim")
    }
    run {
        compileOnly(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:${libs("mixinsquared")}")) { }
        implementation(jarJar("com.github.bawnorton.mixinsquared:mixinsquared-forge:${libs("mixinsquared")}")) { }
    }
    annotationProcessor("org.spongepowered:mixin:${libs("mixin")}:processor")
    implementation("thedarkcolour:kotlinforforge:${libs("kff")}")
    modImplementation("dev.isxander:yet-another-config-lib:${libs("yacl")}")
    run {
        modImplementation("maven.modrinth:timeless-and-classics-zero:${libs("tacz")}")
        implementation("org.apache.commons:commons-math3:3.6.1")
    }

    // Compatibility
    modCompileOnly("maven.modrinth:firstaid:${libs("firstaid")}")
    modCompileOnly("maven.modrinth:legendary-survival-overhaul:${libs("lso")}")
    modCompileOnly("curse.maven:pillagers-gun-948255:${libs("pillagers-gun")}")
    run {
        modCompileOnly("org.valkyrienskies:valkyrienskies-120-forge:${libs("valkyrienskies")}")
        compileOnly("org.valkyrienskies.core:api:${libs("valkyrienskies-core")}")
        compileOnly("org.valkyrienskies.core:internal:${libs("valkyrienskies-core")}")
        compileOnly("org.valkyrienskies.core:util:${libs("valkyrienskies-core")}")
        compileOnly("org.valkyrienskies.core:impl:${libs("valkyrienskies-core")}")
    }
    modCompileOnly("maven.modrinth:cuffed:1.3.13")
    modCompileOnly("maven.modrinth:lr-tactical:${libs("lr-tactical")}")
    modCompileOnly("maven.modrinth:sound-physics-remastered:${libs("sound-physics-remastered")}")

    // Runtime
    modLocalRuntime("maven.modrinth:better-modlist:${libs("better-modlist")}")
    modLocalRuntime("maven.modrinth:cubes-without-borders:${libs("cubes-without-borders")}")
    modLocalRuntime("org.valkyrienskies:valkyrienskies-120-forge:${libs("valkyrienskies")}")
    modLocalRuntime("maven.modrinth:sound-physics-remastered:${libs("sound-physics-remastered")}")
}

resourceProperties {
    properties.putAll(mapOf(
        "loader_version" to libs("forge"),
        "kotlin_loader_version" to libs("kff").substringBefore('.'),
        "kff_version" to libs("kff"),
        "tacz_version_min" to prop("version.target.min"),
        "tacz_version_max" to prop("version.target.max")
    ))
}

stonecutter replacements {
    run {
        val from = arrayOf(
            "net.neoforged.api.distmarker.OnlyIn",
            "net.fabricmc.api.Environment"
        )
        val to = "net.minecraftforge.api.distmarker.OnlyIn"
        from.forEach { string(true, "environment") { replace(it, to) } }
    }
    run {
        val from = arrayOf(
            "net.neoforged.api.distmarker.Dist.CLIENT",
            "net.fabricmc.api.EnvType.CLIENT"
        )
        val to = "net.minecraftforge.api.distmarker.Dist.CLIENT"
        from.forEach { string(true, "environment_client") { replace(it, to) } }
    }
    run {
        val from = arrayOf(
            "net/neoforged/neoforge/common/ModConfigSpec"
        )
        val to = "net/minecraftforge/common/ForgeConfigSpec"
        from.forEach { string(true, "config_spec") { replace(it, to) } }
    }
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
}

publishMods {
    file = tasks.named<Jar>("reobfJar").get().archiveFile
    additionalFiles.from(tasks.sourcesJar.get().archiveFile)
}