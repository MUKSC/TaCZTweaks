import co.uzzu.dotenv.gradle.DotEnvRoot
import dev.kikugie.stonecutter.build.StonecutterBuildExtension

plugins {
    kotlin("jvm")
    id("me.modmuss50.mod-publish-plugin")
}

val env = extensions.getByType<DotEnvRoot>()
val stonecutter = extensions.getByType<StonecutterBuildExtension>()
val loader = stonecutter.node.metadata.project.substringAfterLast('-')
version = "${prop("version")}+${prop("minecraft.version")}-$loader"

base {
    archivesName = prop("archives_base_name")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(prop("java.version"))
        vendor = JvmVendorSpec.JETBRAINS
    }
    withSourcesJar()
}

repositories {
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven")
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://cursemaven.com")
        }
        filter {
            includeGroup("curse.maven")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.parchmentmc.org")
        }
        filter {
            includeGroup("org.parchmentmc.data")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.bawnorton.com/releases")
        }
        filter {
            includeGroup("com.github.bawnorton.mixinsquared")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://repo.spongepowered.org/repository/maven-public")
        }
        filter {
            includeGroupAndSubgroups("org.spongepowered")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.valkyrienskies.org")
        }
        filter {
            includeGroupAndSubgroups("org.valkyrienskies")
            includeGroup("com.github.Rubydesic")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.blamejared.com")
        }
        filter {
            includeGroup("foundry.veil")
            includeGroup("io.github.ocelot")
        }
    }
    mavenCentral()
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.ryanhcode.dev/releases")
    flatDir {
        dirs(rootProject.file("libs"))
    }
}

sourceSets {
    main {
        val sourceSet = this
        for ((name, srcDir) in arrayOf(
            "java" to java,
            "kotlin" to kotlin,
            "resources" to resources
        )) {
            srcDir.srcDirs("src/${sourceSet.name}/$name-$loader")
        }
    }
}

tasks.withType<Jar>().configureEach {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${archiveBaseName.get()}" }
    }
}

val resourceProperties = extensions.create<ResourcePropertiesExtension>("resourceProperties").apply {
    properties.putAll(mapOf(
        "pack_format" to when (val mcVersion = prop("minecraft.version")) {
            "1.20.1" -> "15"
            "1.21.1" -> "34"
            else -> error("Couldn't detect pack_format for version: $mcVersion")
        },
        "java_version" to prop("java.version"),
        "minecraft_version" to prop("minecraft.version"),
        "version" to prop("version"),
        "mod_id" to mod("id"),
        "mod_name" to mod("name"),
        "yacl_version" to libs("yacl")
    ))
}

tasks.processResources {
    val properties = resourceProperties.properties.get()
    inputs.properties(properties)
    doFirst {
        filesMatching(listOf(
            "pack.mcmeta", "*.mixins.json",
            "fabric.mod.json",
            "META-INF/mods.toml",
            "META-INF/neoforge.mods.toml"
        )) {
            expand(properties)
        }
    }
}

publishMods {
    displayName = "${mod("name")} ${prop("version")} for TaCZ ${prop("version.target")}"
    changelog = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText
    type = STABLE
    modLoaders.add(loader)

    modrinth {
        projectId = prop("publish.modrinth")
        // FIXME: projectDescription = providers.fileContents(rootProject.layout.projectDirectory.file("README.md")).asText
        minecraftVersions.add(prop("minecraft.version"))
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
            .orElse(provider { env.fetch("MODRINTH_TOKEN") })

        if (loader == "fabric") requires("fabric-api")
        when (loader) {
            "forge", "neoforge" -> requires("kotlin-for-forge")
            "fabric" -> requires("fabric-language-kotlin")
        }
        requires("yacl")
        when (loader) {
            "forge" -> requires("timeless-and-classics-zero")
            "neoforge" -> requires("tacz-1.21.1")
            "fabric" -> requires("tacz-refabricated")
        }
    }

    curseforge {
        projectId = prop("publish.curseforge")
        minecraftVersions.add(prop("minecraft.version"))
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
            .orElse(provider { env.fetch("CURSEFORGE_TOKEN") })

        clientRequired = true
        serverRequired = true

        if (loader == "fabric") requires("fabric-api")
        when (loader) {
            "forge", "neoforge" -> requires("kotlin-for-forge")
            "fabric" -> requires("fabric-language-kotlin")
        }
        requires("yacl")
        when (loader) {
            "forge" -> requires("timeless-and-classics-zero")
            "neoforge" -> requires("tacz-1-21-1")
            "fabric" -> requires("tacz-refabricated")
        }
    }

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        parent(rootProject.tasks.named("publishGithub"))
    }
}