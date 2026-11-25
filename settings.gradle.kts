@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
  includeBuild("gradle/tools/plugins-projects")
  includeBuild("gradle/tools/plugins-docs")
  includeBuild("gradle/tools/plugins-settings")
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
  }
  versionCatalogs {
    create("local") {
      from(files("gradle/local.versions.toml"))
    }
    create("tools") {
      from(files("gradle/tools.versions.toml"))
    }
  }
}

rootProject.name = "ai-job-tracker"

includeBuild("backend")
includeBuild("frontend")
includeBuild("gradle/tools/plugins-projects")
includeBuild("gradle/tools/plugins-docs")
includeBuild("gradle/tools/plugins-settings")
includeBuild("internal")
includeBuild("records")
includeBuild("tests")
