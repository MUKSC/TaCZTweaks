plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.21")
    implementation("co.uzzu.dotenv.gradle:co.uzzu.dotenv.gradle.gradle.plugin:4.0.0")
    implementation("dev.kikugie:stonecutter:0.9.5")
    implementation("me.modmuss50.mod-publish-plugin:me.modmuss50.mod-publish-plugin.gradle.plugin:1.1.0")
}