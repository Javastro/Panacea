rootProject.name = "Panacea"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.dev.uksrc.org/repository/maven-public/")
        mavenLocal()
    }
}

plugins {
    id("org.javastro.build") version "0.2.8"
}

dependencyResolutionManagement {
    repositories {
        mavenLocal() // ExecutionDM snapshot is not yet published to the UKSRC repository
    }
}
// subprojects are discovered automatically: every directory with a build.gradle.kts
