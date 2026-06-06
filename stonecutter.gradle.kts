import dev.kikugie.stonecutter.data.tree.ProjectNode

plugins {
    id("co.uzzu.dotenv.gradle")
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin")
    id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT" apply false
    id("net.neoforged.moddev.legacyforge") version "2.0.140" apply false
    id("net.neoforged.moddev") version "2.0.140" apply false
}

stonecutter active "1.20.1-forge"

stonecutter parameters {
    val loader = node.metadata.project.substringAfterLast('-')
    constants.match(loader, listOf("fabric", "forge", "neoforge"))
}

stonecutter tasks {
    val comparator = compareBy<ProjectNode> { when (it.metadata.project.substringAfterLast('-')) {
        "fabric" -> 0
        "forge", "neoforge" -> 1
        else -> error("Unknown loader for project: ${it.metadata.project}")
    } }.then(versionComparator)
    order("publishModrinth", comparator)
    order("publishCurseforge", comparator)
}

publishMods {
    displayName = "${mod("name")} ${prop("version")} for TaCZ ${prop("version.target")}"
    changelog = providers.fileContents(layout.projectDirectory.file("CHANGELOG.md")).asText
    type = ALPHA
    dryRun = providers.gradleProperty("publish.dry").map(String::toBoolean)

    github {
        repository = prop("publish.repository")
        commitish = prop("publish.commitish")
        tagName = "v${prop("version")}"
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
            .orElse(provider { env.fetch("GITHUB_TOKEN") })

        allowEmptyFiles = true
    }
}