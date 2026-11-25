package dev.fnc314.aijobtracker.gradletools.docs

import org.gradle.api.Project
import org.jetbrains.dokka.gradle.DokkaExtension

/**
 * Node documentation convention plugin applied to intermediate non-publishing container projects
 * (e.g. `:frontend`, `:frontend:apps`, `:frontend:features`, `:records`, `:gradle:tools`).
 *
 * Acts as an aggregation conduit wiring child leaves and node bundles into the "dokka" configuration
 * without generating duplicate or empty module documentation.
 */
public class NodeDocsConventionPlugin : DocsConventionPlugin() {
  override fun Project.configureDocs() {
    pluginManager.apply("org.jetbrains.dokka")

    extensions.configure(DokkaExtension::class.java) { dokka ->
      dokka.dokkaPublications.named("html") { publication ->
        // Use path-based naming for intermediate nodes
        publication.moduleName.set(path.ifEmpty { ":$name" })
      }
    }
  }
}
