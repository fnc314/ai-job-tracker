package dev.fnc314.aijobtracker.gradletools.settings

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import java.io.File

public class SubsystemSettingsPlugin : Plugin<Settings> {
  override fun apply(settings: Settings) {
    val extension = settings.extensions.create("subsystemSettings", SubsystemSettingsExtension::class.java)

    // Resolve projectRoot
    val resolvedRoot: File = resolveRoot(settings)
    extension.projectRoot.convention(settings.layout.settingsDirectory.dir(resolvedRoot.absolutePath))

    // Enable standard features
    settings.enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

    // Configure repository defaults for pluginManagement & dependencyResolutionManagement
    settings.pluginManagement.repositories { handler ->
      handler.google()
      handler.mavenCentral()
      handler.gradlePluginPortal()
    }

    settings.dependencyResolutionManagement.repositories { handler ->
      handler.google()
      handler.mavenCentral()
    }

    settings.gradle.settingsEvaluated {
      val rootDir = extension.projectRoot.get().asFile

      // Include tool builds
      val toolsToInclude = if (extension.toolBuildsList.isNotEmpty()) {
        extension.toolBuildsList
      } else {
        listOf(
          "gradle/tools/plugins-projects",
          "gradle/tools/plugins-docs",
        )
      }

      for (relPath in toolsToInclude) {
        val buildDir = File(rootDir, relPath)
        if (buildDir.exists()) {
          settings.pluginManagement.includeBuild(buildDir.absolutePath)
        }
      }

      // Configure catalogs
      val catalogs = if (extension.catalogsList.isNotEmpty()) {
        extension.catalogsList
      } else {
        mapOf("libs" to null, "local" to null)
      }

      for ((name, customPath) in catalogs) {
        val relPath = customPath ?: "gradle/$name.versions.toml"
        val catalogFile = File(rootDir, relPath)
        if (catalogFile.exists() && settings.dependencyResolutionManagement.versionCatalogs.findByName(name) == null) {
          settings.dependencyResolutionManagement.versionCatalogs.create(name) { catalog ->
            catalog.from(catalogFile)
          }
        }
      }
    }
  }

  private fun resolveRoot(settings: Settings): File {
    System.getenv("GRADLE_DOCS_ROOT")?.let { File(it) }?.takeIf { it.exists() }?.let { return it }
    System.getenv("WORKSPACE_ROOT")?.let { File(it) }?.takeIf { it.exists() }?.let { return it }

    // Upwards search for gradle/wrapper/gradle-wrapper.properties
    var current: File? = settings.settingsDir
    while (current != null) {
      if (File(current, "gradle/wrapper/gradle-wrapper.properties").exists()) {
        return current
      }
      current = current.parentFile
    }

    return settings.settingsDir.parentFile ?: settings.settingsDir
  }
}
