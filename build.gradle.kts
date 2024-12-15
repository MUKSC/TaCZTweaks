plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.forge.gradle)
    alias(libs.plugins.librarian.forgegradle)
    alias(libs.plugins.mixin)
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

        create("client")
        create("server") {
            args("--nogui")
        }
    }
}

repositories {
    maven("https://thedarkcolour.github.io/KotlinForForge")
    maven("https://maven.bawnorton.com/releases")
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
}

dependencies {
    minecraft(libs.net.minecraftforge.forge)
    implementation(libs.thedarkcolour.kotlinforforge)
    libs.com.github.bawnorton.mixinsquared.run {
        common.run {
            annotationProcessor(this)
            compileOnly(this)
        }
        forge.run {
            jarJar(this) {
                jarJar.ranged(this, "[${libs.versions.mixinsquared.get()},)")
            }
            implementation(this)
        }
    }
    libs.io.github.llamalad7.mixinextras.run {
        common.run {
            annotationProcessor(this)
            compileOnly(this)
        }
        forge.run {
            jarJar(this) {
                jarJar.ranged(this, "[${libs.versions.mixinextras.get()},)")
            }
            implementation(this)
        }
    }
    annotationProcessor(variantOf(libs.org.spongepowered.mixin) { classifier("processor") })

    implementation(fg.deobf(libs.modrinth.tacz.get()))
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

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}