enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
  val rootRepoDir =
    System.getenv("GRADLE_DOCS_ROOT")?.let { java.io.File(it) }?.takeIf { it.exists() }
      ?: System.getenv("WORKSPACE_ROOT")?.let { java.io.File(it) }?.takeIf { it.exists() }
      ?: run {
        var current: java.io.File? = settings.settingsDir
        var found: java.io.File? = null
        while (current != null) {
          if (java.io.File(current, "gradle/wrapper/gradle-wrapper.properties").exists()) {
            found = current
            break
          }
          current = current.parentFile
        }
        found ?: settings.settingsDir.parentFile ?: settings.settingsDir
      }
  includeBuild("$rootRepoDir/gradle/tools/plugins-projects")
  includeBuild("$rootRepoDir/gradle/tools/plugins-docs")
  includeBuild("$rootRepoDir/gradle/tools/plugins-settings")
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
  }
  val rootRepoDir =
    System.getenv("GRADLE_DOCS_ROOT")?.let { java.io.File(it) }?.takeIf { it.exists() }
      ?: System.getenv("WORKSPACE_ROOT")?.let { java.io.File(it) }?.takeIf { it.exists() }
      ?: run {
        var current: java.io.File? = settings.settingsDir
        var found: java.io.File? = null
        while (current != null) {
          if (java.io.File(current, "gradle/wrapper/gradle-wrapper.properties").exists()) {
            found = current
            break
          }
          current = current.parentFile
        }
        found ?: settings.settingsDir.parentFile ?: settings.settingsDir
      }
  versionCatalogs {
    if (findByName("libs") == null) {
      create("libs") {
        from(files("$rootRepoDir/gradle/libs.versions.toml"))
      }
    }
    if (findByName("local") == null) {
      create("local") {
        from(files("$rootRepoDir/gradle/local.versions.toml"))
      }
    }
    if (findByName("tools") == null && java.io.File("$rootRepoDir/gradle/tools.versions.toml").exists()) {
      create("tools") {
        from(files("$rootRepoDir/gradle/tools.versions.toml"))
      }
    }
  }
}