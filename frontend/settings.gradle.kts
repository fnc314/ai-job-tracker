import dev.fnc314.gradle.plugins.settings.projectcollectionsgradlesettingsplugin.ProjectNameMapper

apply(from = "../gradle/tools/settings-plugins/subsystem.settings.gradle.kts")

rootProject.name = "ai-job-tracker_frontend"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("dev.fnc314.gradle.plugins.settings.project-collections-gradle-settings-plugin") version("5.2.2")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.fnc314.gradle.plugins.settings.project-collections-gradle-settings-plugin") version "5.2.2"
}

projectCollections {
    flattenMiddleProjects.set(false)
    registerProjectCollection("apps")
    registerProjectCollection("core")
    registerProjectCollection("features", depth = 2)
}

includeBuild("../records")
includeBuild("../backend")
