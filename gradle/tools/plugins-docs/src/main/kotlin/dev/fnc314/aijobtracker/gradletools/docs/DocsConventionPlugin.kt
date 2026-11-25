package dev.fnc314.aijobtracker.gradletools.docs

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Base sealed class for all Documentation Convention Plugins in the 3-layer architecture.
 *
 * Direct subclasses define the behaviors for Root, Node, and Leaf layers.
 */
public sealed class DocsConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.configureDocs()
  }

  /**
   * Internal configuration hook implemented by layer-specific plugins.
   */
  protected abstract fun Project.configureDocs()
}
