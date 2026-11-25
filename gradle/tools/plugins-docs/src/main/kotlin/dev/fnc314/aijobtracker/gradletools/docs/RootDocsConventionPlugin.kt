package dev.fnc314.aijobtracker.gradletools.docs

import org.gradle.api.Project
import org.jetbrains.dokka.gradle.DokkaExtension

/**
 * Root documentation convention plugin applied to the repository root project.
 *
 * Orchestrates top-level HTML publication, output directory targeting `dokka/`,
 * and aggregates included build and subsystem node publications.
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
  }
}
