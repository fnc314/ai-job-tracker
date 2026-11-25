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

rootProject.name = "ai-job-tracker_backend"

include(":service")
include(":job-post-ranking")
include(":job-post-scraping")
include(":artifact-compilation")
include(":tracker-conventions")
include(":email-classification")
include(":company-metadata-enrichment")
include(":event-chain")
include(":ai:client")
include(":oauth")
includeBuild("../records")
