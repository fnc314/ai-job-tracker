pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("../gradle/tools/plugins-projects")
    includeBuild("../gradle/tools/plugins-docs")
    includeBuild("../gradle/tools/plugins-settings")
}

apply(from = "../gradle/tools/settings-plugins/subsystem.settings.gradle.kts")

rootProject.name = "ai-job-tracker_records"

include(":proto")
include(":parser")
