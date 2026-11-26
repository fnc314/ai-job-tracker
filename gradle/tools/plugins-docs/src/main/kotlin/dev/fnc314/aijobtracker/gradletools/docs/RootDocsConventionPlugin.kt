package dev.fnc314.aijobtracker.gradletools.docs

import org.gradle.api.Project
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask

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

    val pluginJar = layout.projectDirectory.file("gradle/tools/plugins-docs/build/libs/ai-job-tracker_gradle-tools_plugins-docs-4.2.1.jar")

    tasks.withType(DokkaGeneratePublicationTask::class.java).configureEach { task ->
      task.generator.pluginsClasspath.from(pluginJar)
    }
  }
}


