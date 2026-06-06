plugins {
    id("multiloader-common")
    id("fabric-loom")
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources-fabric/${mod("id")}.accesswidener")

    runs {
        named("client") {
            programArgs("--username=Dev")
            runDir = "run/client"
        }

        create("client2") {
            client()
            programArgs("--username=Dev2")
            runDir = "run/clientB"
        }

        named("server") {
            programArgs("--nogui")
            runDir = "run/server"
        }

        configureEach {
            vmArgs("-XX:+AllowEnhancedClassRedefinition")
            isIdeConfigGenerated = true
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
            maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven")
        }
        filter {
            includeGroup("fuzs.forgeconfigapiport")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.ladysnake.org/releases")
        }
        filter {
            includeGroup("dev.onyxstudios.cardinal-components-api")
            includeGroup("org.ladysnake.cardinal-components-api")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://mvn.devos.one/snapshots")
        }
        filter {
            includeGroup("com.simibubi.create")
            includeGroup("com.tterrag.registrate_fabric")
            includeGroup("io.github.tropheusj")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://mvn.devos.one/releases")
        }
        filter {
            includeGroup("io.github.fabricators_of_create.Porting-Lib")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.createmod.net")
        }
        filter {
            includeGroup("dev.engine-room.flywheel")
            includeGroup("net.createmod.ponder")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.jamieswhiteshirt.com/libs-release")
        }
        filter {
            includeGroup("com.jamieswhiteshirt")
        }
    }
    maven("https://jitpack.io") {
        content {
            includeGroup("com.github.FiguraMC.luaj")
        }
    }
    maven("https://maven.terraformersmc.com")
}

dependencies {
    minecraft("com.mojang:minecraft:${prop("minecraft.version")}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${prop("minecraft.version")}:${libs("parchment")}@zip")
    })
    include(implementation(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:${libs("mixinsquared")}")) { }) { }
    modImplementation("net.fabricmc:fabric-loader:${libs("fabric.loader")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${libs("fabric.kotlin")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${libs("fabric.api")}")
    modImplementation("dev.isxander:yet-another-config-lib:${libs("yacl")}")
    run {
        modImplementation("maven.modrinth:tacz-refabricated:${libs("tacz")}")
        implementation("com.maydaymemory:mae:1.1.1")
        implementation("org.apache.commons:commons-math3:3.6.1")
        localRuntime("com.github.FiguraMC.luaj:luaj-core:3.0.8-figura")
        localRuntime("com.github.FiguraMC.luaj:luaj-jse:3.0.8-figura")
        localRuntime("org.apache.bcel:bcel:6.6.1")
        if (sc.current.version == "1.20.1") {
            modImplementation("libs:simplebedrockmodel-fabric:2.3.0.1+mc1.20.1")  // https://github.com/Sh1roCu/TACZ-Refabricated/blob/1.20.1/libs
            modLocalRuntime("dev.onyxstudios.cardinal-components-api:cardinal-components-base:5.2.3")
            modLocalRuntime("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:5.2.3")
            modLocalRuntime("maven.modrinth:modernkeybinding:1.20.X-1.2.0")
            modImplementation("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:8.0.3")
        } else if (sc.current.version == "1.21.1") {
            modImplementation("libs:simplebedrockmodel-fabric:2.3.0.1+mc1.21.1")  // https://github.com/Sh1roCu/TACZ-Refabricated/blob/1.21.1/libs
            modLocalRuntime("org.ladysnake.cardinal-components-api:cardinal-components-base:6.1.2")
            modLocalRuntime("org.ladysnake.cardinal-components-api:cardinal-components-entity:6.1.2")
            modImplementation("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:21.1.6")
        }
    }

    // Compatibility
    modCompileOnly("com.terraformersmc:modmenu:${libs("modmenu")}")
    if (sc.current.version == "1.20.1") {
        modCompileOnly("org.valkyrienskies:valkyrienskies-120-fabric:${libs("valkyrienskies")}")
        modLocalRuntime("org.valkyrienskies:valkyrienskies-120-fabric:${libs("valkyrienskies")}") {
            isTransitive = false
        }
        compileOnly("org.valkyrienskies.core:api:${libs("valkyrienskies-core")}")
        compileOnly("org.valkyrienskies.core:internal:${libs("valkyrienskies-core")}")
        compileOnly("org.valkyrienskies.core:util:${libs("valkyrienskies-core")}")
        compileOnly("org.valkyrienskies.core:impl:${libs("valkyrienskies-core")}")
    }
    if (sc.current.version == "1.21.1") {
        run {
            modCompileOnly("dev.ryanhcode.sable:sable-fabric-1.21.1:${libs("sable")}") {
                isTransitive = false
            }
            modCompileOnly("dev.ryanhcode.sable-companion:sable-companion-fabric-1.21.1:${libs("sable-companion")}")
        }
    }
    modCompileOnly("maven.modrinth:sound-physics-remastered:${libs("sound-physics-remastered")}")

    // Runtime
    modLocalRuntime("com.terraformersmc:modmenu:${libs("modmenu")}")
    modLocalRuntime("maven.modrinth:cubes-without-borders:${libs("cubes-without-borders")}")
    if (sc.current.version == "1.21.1") modLocalRuntime("maven.modrinth:keybind-fix-plus:${libs("keybind-fix-plus")}")
    modLocalRuntime("maven.modrinth:sound-physics-remastered:${libs("sound-physics-remastered")}")
}

resourceProperties {
    properties.putAll(mapOf(
        "loader_version" to libs("fabric.loader"),
        "fabric_api_version" to libs("fabric.api"),
        "kotlin_loader_version" to libs("fabric.kotlin"),
        "tacz_version_min" to prop("version.target.min")
    ))
}

stonecutter replacements {
    run {
        val from = arrayOf(
            "net.minecraftforge.api.distmarker.OnlyIn",
            "net.neoforged.api.distmarker.OnlyIn"
        )
        val to = "net.fabricmc.api.Environment"
        from.forEach { string(true, "environment") { replace(it, to) } }
    }
    run {
        val from = arrayOf(
            "net.minecraftforge.api.distmarker.Dist.CLIENT",
            "net.neoforged.api.distmarker.Dist.CLIENT"
        )
        val to = "net.fabricmc.api.EnvType.CLIENT"
        from.forEach { string(true, "environment_client") { replace(it, to) } }
    }
    run {
        val from = arrayOf(
            "net/minecraftforge/common/ForgeConfigSpec"
        )
        val to = "net/neoforged/neoforge/common/ModConfigSpec"
        from.forEach { string(stonecutter.current.parsed >= "1.21", "config_spec") { replace(it, to) } }
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
}