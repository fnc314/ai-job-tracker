package dev.fnc314.aijobtracker.gradletools.docs.catalog

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider

/**
 * Type-safe bundle lookup contract scoped specifically to the 'local' VersionCatalog.
 */
public enum class LocalCatalogBundles(public val bundleKey: String) {
  RECORDS("records"),
  BACKEND("backend"),
  FRONTEND("frontend"),
  FRONTEND_APPS("frontend-apps"),
  FRONTEND_CORE("frontend-core"),
  FRONTEND_FEATURES("frontend-features"),
  INTERNAL("internal"),
  TOOLS("tools"),
  ;

  public fun find(project: Project): Provider<ExternalModuleDependencyBundle> =
    project.localCatalog.findBundle(bundleKey).orElseThrow {
      IllegalArgumentException("Missing bundle '$bundleKey' in local catalog")
    }

  public fun applyToDocs(project: Project) {
    project.dependencies.addProvider("dokka", find(project))
  }
}

/** Extension to retrieve the 'local' VersionCatalog */
internal val Project.localCatalog: VersionCatalog
  get() = extensions.getByType(VersionCatalogsExtension::class.java).named("local")
