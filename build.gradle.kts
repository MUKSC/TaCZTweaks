plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.forge.gradle)
    alias(libs.plugins.librarian.forgegradle)
    alias(libs.plugins.mixin)
    alias(libs.plugins.mod.publish)
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

mixin {
    add(sourceSets.main.get(), "tacztweaks.refmap.json")
    config("tacztweaks.mixins.json")
}

minecraft {
    accessTransformer("src/main/resources/META-INF/accesstransformer.cfg")
    mappings(mapOf(
        "channel" to "parchment",
        "version" to "${libs.versions.parchment.get()}-${libs.versions.minecraft.asProvider().get()}"
    ))
    runs {
        configureEach {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            mods {
                create("tacztweaks") {
                    source(sourceSets["main"])
                }
            }
        }

        val client = create("client")
        create("client2") {
            parent(client)
            args("--username", "Dev2")
        }
        create("server") {
            args("--nogui")
        }
    }
}

repositories {
    mavenCentral()
    maven("https://thedarkcolour.github.io/KotlinForForge")
    maven("https://maven.bawnorton.com/releases")
    maven("https://repo.spongepowered.org/repository/maven-public")
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        forRepositories(fg.repository)
        filter {
            includeGroup("maven.modrinth")
        }
    }
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.valkyrienskies.org")
}

dependencies {
    minecraft(libs.net.minecraftforge.forge)
    implementation(libs.thedarkcolour.kotlinforforge)
    compileOnly(annotationProcessor(libs.com.github.bawnorton.mixinsquared.common.get())) { }
    implementation(jarJar(libs.com.github.bawnorton.mixinsquared.forge.get())) {
        jarJar.ranged(this, "[${libs.versions.mixinsquared.get()},)")
    }
    compileOnly(annotationProcessor(libs.io.github.llamalad7.mixinextras.common.get())) { }
    implementation(jarJar(libs.io.github.llamalad7.mixinextras.forge.get())) {
        jarJar.ranged(this, "[${libs.versions.mixinextras.get()},)")
    }
    annotationProcessor(variantOf(libs.org.spongepowered.mixin) { classifier("processor") })

    implementation(fg.deobf(libs.dev.isxander.yacl.get()))
    implementation(fg.deobf(libs.modrinth.tacz.get()))
    implementation(fg.deobf(libs.modrinth.firstaid.get()))
    implementation(fg.deobf(libs.org.valkyrienskies.forge.get()))
    compileOnly(libs.org.valkyrienskies.core.api)
    compileOnly(libs.org.valkyrienskies.core.api.game)
    compileOnly(libs.org.valkyrienskies.core.util)
    compileOnly(libs.org.valkyrienskies.core.impl)
    runtimeOnly(fg.deobf(libs.modrinth.neat.get()))
}

tasks.processResources {
    val properties = mapOf(
        "id" to project.property("mod_id") as String,
        "version" to project.version as String,
        "name" to project.property("mod_name") as String,
        "minecraft_version" to libs.versions.minecraft.asProvider().get(),
        "minecraft_version_range" to libs.versions.minecraft.range.get(),
        "forge_version" to libs.versions.forge.asProvider().get(),
        "forge_version_range" to "[${libs.versions.forge.asProvider().get().substringBefore('.')},)",
        "loader_version_range" to "[${libs.versions.forge.asProvider().get().substringBefore('.')},)",
        "tacz_version_range" to "(${libs.versions.tacz.previous.get()},${libs.versions.tacz.next.get()})",
        "kotlinforforge_version_range" to "[${libs.versions.kotlinforforge.get().substringBeforeLast('.')},)"
    )
    inputs.properties(properties)
    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) { expand(properties) }
}

tasks.jar {
    manifest {
        attributes(mapOf(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        ))
    }
    from("LICENSE") {
        rename { "${it}_${archiveBaseName.get()}" }
    }
    finalizedBy("reobfJar")
}

tasks.jarJar {
    from("LICENSE") {
        rename { "${it}_${archiveBaseName.get()}" }
    }
    finalizedBy("reobfJarJar")
}

val packageExamplePack = tasks.register<Zip>("packageExamplePack") {
    from(layout.projectDirectory.dir("tacz-tweaks-example-pack"))
    destinationDirectory = layout.buildDirectory
    archiveFileName = "tacz-tweaks-example-pack.zip"
}

tasks.build {
    dependsOn(packageExamplePack)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

publishMods {
    displayName = "${project.findProperty("mod_name")} ${project.version}"
    changelog = providers.fileContents(layout.projectDirectory.file("CHANGELOG.md")).asText
    file = tasks.jarJar.get().archiveFile
    additionalFiles.from(packageExamplePack.get().archiveFile)
    type = STABLE
    modLoaders.add("forge")

    modrinth {
        projectId = project.findProperty("modrinth_id") as String
        projectDescription = providers.fileContents(layout.projectDirectory.file("README.md")).asText
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(libs.versions.minecraft.list.get().split(','))

        requires("kotlin-for-forge")
        requires("yacl")
        requires("timeless-and-classics-zero")
    }

    curseforge {
        projectId = project.findProperty("curseforge_id") as String
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.addAll(libs.versions.minecraft.list.get().split(','))

        clientRequired = true
        serverRequired = true

        requires("kotlin-for-forge")
        requires("yacl")
        requires("timeless-and-classics-zero")
    }

    github {
        repository = project.findProperty("repository") as String
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        commitish = "main"
        tagName = "v${project.version}"
    }
}