package dev.fnc314.aijobtracker.gradletools.docs

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.jetbrains.dokka.gradle.DokkaExtension

/**
 * Root documentation convention plugin applied to the repository root project.
 *
 * Orchestrates top-level HTML publication, output directory targeting `dokka/`,
 * aggregates included build and subsystem node publications, and loads the
 * native [dev.fnc314.aijobtracker.gradletools.docs.tree.NavigationTreePlugin]
 * via Dokka's plugin SPI to organize multi-module navigation into a collapsible tree.
 */
public class RootDocsConventionPlugin : DocsConventionPlugin() {
  override fun Project.configureDocs() {
    pluginManager.apply("org.jetbrains.dokka")

    extensions.configure(DokkaExtension::class.java) { dokka ->
      dokka.dokkaPublications.named("html") { publication ->
        publication.moduleName.set("AI Job Search & Application OS Documentation")
        publication.outputDirectory.set(layout.projectDirectory.dir("dokka"))
      }
    }

    // Conceal the Dokka plugin dependency by resolving it through the version catalog
    val versionCatalogs = extensions.getByType(VersionCatalogsExtension::class.java)
    val toolsCatalog = versionCatalogs.find("tools").orElseGet {
      versionCatalogs.named("local")
    }
    val docsPlugin = toolsCatalog.findLibrary("tools-plugins-docs").orElseThrow {
      IllegalStateException("Unable to resolve 'tools-plugins-docs' library from version catalogs")
    }
    dependencies.addProvider("dokkaPlugin", docsPlugin)
  }
}


