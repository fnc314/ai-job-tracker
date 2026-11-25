rootProject.name = "ai-job-tracker_gradle-tools_plugins-projects"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("../plugins-docs")
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../../libs.versions.toml"))
        }
        create("local") {
            from(files("../../local.versions.toml"))
        }
        create("tools") {
            from(files("../../tools.versions.toml"))
        }
    }
}
